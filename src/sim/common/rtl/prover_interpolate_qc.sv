// synthesis VERILOG_INPUT_VERSION SYSTEMVERILOG_2012
// special-purpose interpolator for quadratic or cubic functions

// After each round of sumcheck, P tells V a low-degree polynomial.
// P is free to specify this polynomial any way it likes, but the
// best choice is clearly "whatever requires the least work from V"
// (since the whole point is to save V work).
//
// To that end: in this impl, P specifies the polynomial as coefficients,
// i.e., it does the work of interpolating for V. Note that this doesn't
// give it any more chance to fool V than it already had.
//
// In Giraffe, the low-degree polynomial is either 2nd or 3rd order,
// and it is always evaluated at 0, 1, -1, and 2 (the latter only for cubic
// polynomials). We can thus build a very specialized interpolating circuit.
//
// Quadratic interpolation from 0, 1, -1:
//
//   f(x) = c0 + c1 * x + c2 * x^2
//
//   We have y0, y1, ym1.
//      y0  = f(0)  = c0
//      y1  = f(1)  = c0 + c1 + c2
//      ym1 = f(-1) = c0 - c1 + c2
//
//   let a  = -y0
//
//   let c0 = y0
//   let c2 = a + (y1 + ym1) / 2
//   let c1 = y1 - c2 + a
//
//   return c0, c1, c2
//
// Cubic interpolation from 0, 1, -1, 2:
//
//   f(x) = c0 + c1 * x + c2 * x^2 + c3 * x^3
//
//   We have y0, y1, ym1, y2:
//      y0  = f(0)  = c0
//      y1  = f(1)  = c0 + c1 + c2 + c3
//      ym1 = f(-1) = c0 - c1 + c2 - c3
//      y2  = f(2)  = c0 + 2 * c1 + 4 * c2 + 8 * c3
//
//   let a = -1 * ym1
//   let b = (y1 + ym1) / 2 = c0 + c2
//   let c = (y1 + a) / 2   = c1 + c3
//   let d = (y2 + a) / 3   = c1 + c2 + 3 * c3
//
//   let c0 = y0
//   let c2 = b - y0
//   let c3 = (d - c - c2) / 2
//   let c1 = (c - c3)
//
// Finally, note that we can do essentially all of these operations with
// addition rather than with multiplication (see field_negate and field_halve).

`ifndef __module_prover_interpolate_qc
`include "simulator.v"
`include "field_arith_defs.v"
`include "field_adder.sv"
`include "field_halve.sv"
`include "field_multiplier.sv"
module prover_interpolate_qc
    ( input                 clk
    , input                 rstb

    , input                 en

    , input                 cubic   // else quadratic
    , input  [`F_NBITS-1:0] y_in [3:0]

    , output [`F_NBITS-1:0] c_out [3:0]

    , output                ready_pulse
    , output                ready
    );

reg [`F_NBITS-1:0] add_in0_reg [1:0], add_in0_next [1:0];
reg [`F_NBITS-1:0] add_in1_reg [1:0], add_in1_next [1:0];
reg [1:0] add_en_reg, add_en_next;
wire [1:0] add_ready;
wire all_add_ready = &(add_ready);
wire any_add_en = |(add_en_reg);
wire [`F_NBITS-1:0] add_out [1:0];
wire add_ok = all_add_ready & ~any_add_en;

reg [`F_NBITS-1:0] hlv_in_reg, hlv_in_next;
reg hlv_en_reg, hlv_en_next;
wire hlv_ready;
wire [`F_NBITS-1:0] hlv_out;
wire hlv_ok = hlv_ready & ~hlv_en_reg;

reg [`F_NBITS-1:0] mul_in0_reg, mul_in0_next;
reg [`F_NBITS-1:0] mul_in1_reg, mul_in1_next;
reg mul_en_reg, mul_en_next;
wire mul_ready;
wire [`F_NBITS-1:0] mul_out;
wire mul_ok = mul_ready & ~mul_en_reg;

reg [`F_NBITS-1:0] tmp_reg, tmp_next;

reg [`F_NBITS-1:0] c_out_reg [3:0], c_out_next [3:0];
assign c_out = c_out_reg;

enum { ST_IDLE, ST_QUAD0, ST_QUAD1, ST_QUAD2, ST_QUAD3, ST_QUAD4, ST_CUB0, ST_CUB1, ST_CUB2, ST_CUB3, ST_CUB4, ST_CUB5, ST_CUB6, ST_CUB7, ST_CUB8, ST_CUB9 } state_reg, state_next;

reg en_dly, ready_dly;
wire start = en & ~en_dly;
assign ready = (state_reg == ST_IDLE) & ~start;
assign ready_pulse = ready & ~ready_dly;

integer InstC;
`ALWAYS_COMB begin
    for (InstC = 0; InstC < 4; InstC = InstC + 1) begin
        c_out_next[InstC] = c_out_reg[InstC];
    end
    for (InstC = 0; InstC < 2; InstC = InstC + 1) begin
        add_in0_next[InstC] = add_in0_reg[InstC];
        add_in1_next[InstC] = add_in1_reg[InstC];
    end
    tmp_next = tmp_reg;
    hlv_in_next = hlv_in_reg;
    mul_in0_next = mul_in0_reg;
    mul_in1_next = mul_in1_reg;
    add_en_next = 2'b0;
    hlv_en_next = 1'b0;
    mul_en_next = 1'b0;
    state_next = state_reg;

    case (state_reg)
        ST_IDLE: begin
            if (start) begin
                c_out_next[0] = y_in[0];
                add_in0_next[0] = y_in[1];      // y1 + ym1
                add_in1_next[0] = y_in[2];
                add_en_next[0] = 1'b1;

                add_in1_next[1] = `F_Q_P1_MI;
                add_en_next[1] = 1'b1;
                if (cubic) begin
                    add_in0_next[1] = ~y_in[2]; // -ym1
                    state_next = ST_CUB0;
                end else begin
                    c_out_next[3] = {(`F_NBITS){1'b0}};
                    add_in0_next[1] = ~y_in[0]; // -y0
                    state_next = ST_QUAD0;
                end
            end
        end

        ST_QUAD0: begin
            if (add_ok) begin
                hlv_in_next = add_out[0];       // (y1 + ym1) / 2
                hlv_en_next = 1'b1;

                add_in0_next[0] = add_out[1];   // y1 - y0
                add_in1_next[0] = y_in[1];
                add_en_next[0] = 1'b1;

                state_next = ST_QUAD1;
            end
        end

        ST_QUAD1: begin
            if (add_ok & hlv_ok) begin
                add_in0_next[1] = add_out[1];   // (y1 + ym1) / 2 - y0
                add_in1_next[1] = hlv_out;
                add_en_next[1] = 1'b1;

                state_next = ST_QUAD2;
            end
        end

        ST_QUAD2: begin
            if (add_ok) begin
                c_out_next[2] = add_out[1];     // c2

                add_in0_next[1] = ~add_out[1];  // -c2
                add_in1_next[1] = `F_Q_P1_MI;
                add_en_next[1] = 1'b1;

                state_next = ST_QUAD3;
            end
        end

        ST_QUAD3: begin
            if (add_ok) begin
                add_in0_next[0] = add_out[1];   // y1 - y0 - c2
                add_in1_next[0] = add_out[0];
                add_en_next[0] = 1'b1;

                state_next = ST_QUAD4;
            end
        end

        ST_QUAD4: begin
            if (add_ok) begin
                c_out_next[1] = add_out[0];     // c1

                state_next = ST_IDLE;
            end
        end

        ST_CUB0: begin
            if (add_ok) begin
                hlv_in_next = add_out[0];       // (y1 + ym1) / 2
                hlv_en_next = 1'b1;

                add_in0_next[0] = add_out[1];   // y2 - ym1
                add_in1_next[0] = y_in[3];
                add_en_next[0] = 1'b1;

                add_in0_next[1] = add_out[1];   // y1 - ym1
                add_in1_next[1] = y_in[1];
                add_en_next[1] = 1'b1;

                state_next = ST_CUB1;
            end
        end

        ST_CUB1: begin
            if (add_ok & hlv_ok) begin
                tmp_next = hlv_out;             // b

                mul_in0_next = add_out[0];      // (y2 - ym1) / 3
                mul_in1_next = `F_THIRD;
                mul_en_next = 1'b1;

                hlv_in_next = add_out[1];       // (y1 - ym1) / 2
                hlv_en_next = 1'b1;

                add_in0_next[1] = ~y_in[0];     // -y0
                add_in1_next[1] = `F_Q_P1_MI;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB2;
            end
        end

        ST_CUB2: begin
            if (add_ok) begin  // no need to wait for mul or hlv here; don't need those outputs yet
                add_in0_next[1] = add_out[1];   // b - y0
                add_in1_next[1]  = tmp_reg;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB3;
            end
        end

        ST_CUB3: begin
            if (add_ok & hlv_ok) begin
                c_out_next[2] = add_out[1];     // c2

                add_in0_next[1] = add_out[1];   // c2 + c
                add_in1_next[1] = hlv_out;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB4;
            end
        end

        ST_CUB4: begin
            if (add_ok) begin
                add_in0_next[1] = ~add_out[1];  // -(c2 + c)
                add_in1_next[1] = `F_Q_P1_MI;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB5;
            end
        end

        ST_CUB5: begin
            if (add_ok & mul_ok) begin
                add_in0_next[0] = add_out[1];   // d - (c2 + c)
                add_in1_next[0] = mul_out;
                add_en_next[0] = 1'b1;

                state_next = ST_CUB6;
            end
        end

        ST_CUB6: begin
            if (add_ok) begin
                tmp_next = hlv_out;             // c

                hlv_in_next = add_out[0];       // (d - (c2 + c)) / 2
                hlv_en_next = 1'b1;

                state_next = ST_CUB7;
            end
        end

        ST_CUB7: begin
            if (hlv_ok) begin
                c_out_next[3] = hlv_out;        // c3

                add_in0_next[1] = ~hlv_out;     // -c3
                add_in1_next[1] = `F_Q_P1_MI;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB8;
            end
        end

        ST_CUB8: begin
            if (add_ok) begin
                add_in0_next[1] = add_out[1];   // c - c3
                add_in1_next[1] = tmp_reg;
                add_en_next[1] = 1'b1;

                state_next = ST_CUB9;
            end
        end

        ST_CUB9: begin
            if (add_ok) begin
                c_out_next[1] = add_out[1];     // c1

                state_next = ST_IDLE;
            end
        end
    endcase
end

integer InstF;
`ALWAYS_FF @(posedge clk or negedge rstb) begin
    if (~rstb) begin
        en_dly <= 1'b1;
        ready_dly <= 1'b1;
        state_reg <= ST_IDLE;
        for (InstF = 0; InstF < 4; InstF = InstF + 1) begin
            c_out_reg[InstF] <= {(`F_NBITS){1'b0}};
        end
        for (InstF = 0; InstF < 2; InstF = InstF + 1) begin
            add_in0_reg[InstF] <= {(`F_NBITS){1'b0}};
            add_in1_reg[InstF] <= {(`F_NBITS){1'b0}};
        end
        tmp_reg <= {(`F_NBITS){1'b0}};
        hlv_in_reg <= {(`F_NBITS){1'b0}};
        mul_in0_reg <= {(`F_NBITS){1'b0}};
        mul_in1_reg <= {(`F_NBITS){1'b0}};
        add_en_reg <= 2'b0;
        hlv_en_reg <= 1'b0;
        mul_en_reg <= 1'b0;
    end else begin
        en_dly <= en;
        ready_dly <= ready;
        state_reg <= state_next;
        for (InstF = 0; InstF < 4; InstF = InstF + 1) begin
            c_out_reg[InstF] <= c_out_next[InstF];
        end
        for (InstF = 0; InstF < 2; InstF = InstF + 1) begin
            add_in0_reg[InstF] <= add_in0_next[InstF];
            add_in1_reg[InstF] <= add_in1_next[InstF];
        end
        tmp_reg <= tmp_next;
        hlv_in_reg <= hlv_in_next;
        mul_in0_reg <= mul_in0_next;
        mul_in1_reg <= mul_in1_next;
        add_en_reg <= add_en_next;
        hlv_en_reg <= hlv_en_next;
        mul_en_reg <= mul_en_next;
    end
end

field_adder iAdd0
    ( .clk          (clk)
    , .rstb         (rstb)
    , .en           (add_en_reg[0])
    , .a            (add_in0_reg[0])
    , .b            (add_in1_reg[0])
    , .ready_pulse  ()
    , .ready        (add_ready[0])
    , .c            (add_out[0])
    );

field_adder iAdd1
    ( .clk          (clk)
    , .rstb         (rstb)
    , .en           (add_en_reg[1])
    , .a            (add_in0_reg[1])
    , .b            (add_in1_reg[1])
    , .ready_pulse  ()
    , .ready        (add_ready[1])
    , .c            (add_out[1])
    );

field_multiplier iMul
    ( .clk          (clk)
    , .rstb         (rstb)
    , .en           (mul_en_reg)
    , .a            (mul_in0_reg)
    , .b            (mul_in1_reg)
    , .ready_pulse  ()
    , .ready        (mul_ready)
    , .c            (mul_out)
    );

field_halve iHalve
    ( .clk          (clk)
    , .rstb         (rstb)
    , .en           (hlv_en_reg)
    , .a            (hlv_in_reg)
    , .ready_pulse  ()
    , .ready        (hlv_ready)
    , .c            (hlv_out)
    );

endmodule
`define __module_prover_interpolate_qc
`endif // __module_prover_interpolate_qc

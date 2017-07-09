// synthesis VERILOG_INPUT_VERSION SYSTEMVERILOG_2012
//
// NOTE: this file is autogenerated! Modifications will be lost.
//
// Verifier layer

`include "simulator.v"
`include "field_arith_defs.v"
`include "vpintf_defs.v"
`include "verifier_layer.sv"
module verifier_layer_top () ;

{0}
localparam nInBits = $clog2(nInputs);
localparam nOutBits = $clog2(nGates);
localparam nCopies = 1 << nCopyBits;
localparam lastCoeff = nInBits < 3 ? 3 : nInBits;

// values from previous layer
reg [nMuxSels-1:0] muxsel_reg;
reg [`F_NBITS-1:0] expect_val;
reg [`F_NBITS-1:0] z1 [nOutBits-1:0];
reg [`F_NBITS-1:0] z2 [nCopyBits-1:0];

// values from prover
integer recvlen;
reg [`F_NBITS-1:0] c_in [lastCoeff:0];

// this layer's coins
reg [`F_NBITS-1:0] w1 [nInBits-1:0];
reg [`F_NBITS-1:0] w2 [nInBits-1:0];
reg [`F_NBITS-1:0] w3 [nCopyBits-1:0];
reg [`F_NBITS-1:0] tau_final;
reg [63:0] field_counts [5:0];

wire [`F_NBITS-1:0] lay_out;
wire [`F_NBITS-1:0] tau_out;
wire [`F_NBITS-1:0] z1_out [nInBits-1:0];
wire okay, ready, fin_layer;
reg clk, rstb, restart;

enum {{ ST_START, ST_RECV, ST_RUN_ST, ST_RUN }} state_reg, state_next;
wire en_lay = state_reg == ST_RUN_ST;

localparam nChiBits = nInBits > nOutBits ? nInBits : nOutBits;
wire [`F_NBITS-1:0] wpr [4:0];
wire [`F_NBITS-1:0] w1x [nInputs-1:0], w2x [nInputs-1:0];
genvar FVar;
generate
    if (defDebug == 1) begin
        for (FVar = 0; FVar < 5; FVar = FVar + 1) begin
            assign wpr[FVar] = iLayer.iPreds.wpred_reg[FVar];
        end
        for (FVar = 0; FVar < nInputs; FVar = FVar + 1) begin
            assign w1x[FVar] = iLayer.iPreds.w1_chis_reg[FVar];
            assign w2x[FVar] = iLayer.iPreds.w2_chis_reg[FVar];
        end
    end
endgenerate

integer i;
initial begin
    $dumpfile("verifier_layer_{1}.fst");
    $dumpvars;
    if (defDebug == 1) begin
        for (i = 0; i < nOutBits; i = i + 1) begin
            $dumpvars(0, iLayer.z1_vals[i]);
        end
        for (i = 0; i < nCopyBits; i = i + 1) begin
            $dumpvars(0, iLayer.z2_vals[i]);
        end
        for (i = 0; i < nInBits; i = i + 1) begin
            $dumpvars(0, iLayer.w1_vals[i]);
        end
        for (i = 0; i < nInBits; i = i + 1) begin
            $dumpvars(0, iLayer.w2_vals[i]);
        end
        for (i = 0; i < nCopyBits; i = i + 1) begin
            $dumpvars(0, iLayer.w3_vals[i]);
        end
        for (i = 0; i < lastCoeff + 1; i = i + 1) begin
            $dumpvars(0, iLayer.c_in[i]);
        end
        for (i = 0; i < 5; i = i + 1) begin
            $dumpvars(0, wpr[i]);
        end
        for (i = 0; i < nGates; i = i + 1) begin
            $dumpvars(0, iLayer.iPreds.gate_fn_vals[i], iLayer.iPreds.gate_in0_vals[i], iLayer.iPreds.gate_in1_vals[i]);
        end
        for (i = 0; i < nInputs; i = i + 1) begin
            $dumpvars(0, w1x[i], w2x[i]);
        end
        for (i = 0; i < 1 << nChiBits; i = i + 1) begin
            $dumpvars(0, iLayer.iPreds.z1_chis[i], iLayer.iPreds.w1_w2_chis[i]);
            $dumpvars(0, iLayer.iPreds.chis_out[i]);
        end
        for (i = 0; i < nChiBits; i = i + 1) begin
            $dumpvars(0, iLayer.iPreds.chis_in[i]);
        end
    end
    $display("id #%d", $vpintf_init(`V_TYPE_LAY, layNum));
    clk = 0;
    rstb = 1;
    recvlen = lastCoeff + 1;
    state_reg = ST_START;
    #1 rstb = 0;
    #3 rstb = 1;
end

`ALWAYS_COMB begin
    state_next = state_reg;

    case (state_reg)
        ST_START: begin
            recvlen = lastCoeff + 1;
            state_next = ST_RECV;
        end

        ST_RECV: begin
            state_next = ST_RUN_ST;
        end

        ST_RUN_ST, ST_RUN: begin
            if (~en_lay & ready) begin
                if (fin_layer) begin
                    state_next = ST_START;
                end else begin
                    recvlen = lastCoeff + 1;
                    state_next = ST_RECV;
                end
            end else begin
                state_next = ST_RUN;
            end
        end
    endcase
end

`ALWAYS_FF @(clk) begin
    clk <= #1 ~clk;
end

`ALWAYS_FF @(posedge clk or negedge rstb) begin
    if (~rstb) begin
        state_reg <= ST_START;
    end else begin
        state_reg <= state_next;
        if (state_reg == ST_START) begin
            if (defDebug == 1) $display("waiting (V_RECV_MUXSEL)");
            $vpintf_recv(`V_RECV_MUXSEL, 1, muxsel_reg);
            if (defDebug == 1) $display("got (V_RECV_MUXSEL)");

            if (defDebug == 1) $display("waiting (V_RECV_EXPECT)");
            $vpintf_recv(`V_RECV_EXPECT, 1, expect_val);
            if (defDebug == 1) $display("got (V_RECV_EXPECT)");

            if (defDebug == 1) $display("waiting (V_RECV_Z1)");
            $vpintf_recv(`V_RECV_Z1, nOutBits, z1);
            if (defDebug == 1) $display("got (V_RECV_Z1)");

            if (defDebug == 1) $display("waiting (V_RECV_Z2)");
            $vpintf_recv(`V_RECV_Z2, nCopyBits, z2);
            if (defDebug == 1) $display("got (V_RECV_Z2)");

            randomize_vals();
            restart <= 1'b1;
        end else if (state_reg == ST_RECV) begin
            if (defDebug == 1) $display("waiting (V_RECV_COEFFS)");
            $vpintf_recv(`V_RECV_COEFFS, recvlen, c_in);
            if (defDebug == 1) $display("got (V_RECV_COEFFS)");

            if (defDebug == 1) $display("got %d coeffs", recvlen);
        end else if (~en_lay & ready & (state_reg == ST_RUN)) begin
            if (fin_layer) begin
                if (okay) begin
                    $vpintf_send(`V_SEND_OKAY, 0, lay_out);
                end else begin
                    $vpintf_send(`V_SEND_NOKAY, 0, lay_out);
                end
                $vpintf_send(`V_SEND_TAU, 1, tau_final);
                $vpintf_send(`V_SEND_EXPECT, 1, lay_out);
                $vpintf_send(`V_SEND_Z1, nInBits, z1_out);
                $vpintf_send(`V_SEND_Z2, nCopyBits, w3);
                $f_getcnt(field_counts);
                $vpintf_send(`V_SEND_COUNTS, 6, field_counts);
                $f_rstcnt();
            end else begin
                $vpintf_send(`V_SEND_TAU, 1, tau_out);
            end
            restart <= 1'b0;
        end
    end
end

verifier_layer
   #( .nGates       (nGates)
    , .nInputs      (nInputs)
    , .nMuxSels     (nMuxSels)
    , .nCopyBits    (nCopyBits)
    , .nParBits     (nParBitsVLay)
    , .gates_fn     (gates_fn)
    , .gates_in0    (gates_in0)
    , .gates_in1    (gates_in1)
    , .gates_mux    (gates_mux)
    ) iLayer
    ( .clk          (clk)
    , .rstb         (rstb)
    , .en           (en_lay)
    , .restart      (restart)
    , .mux_sel      (muxsel_reg)
    , .c_in         (c_in)
    , .val_in       (expect_val)
    , .lay_out      (lay_out)
    , .z1_vals      (z1)
    , .z2_vals      (z2)
    , .w1_vals      (w1)
    , .w2_vals      (w2)
    , .w3_vals      (w3)
    , .tau_final    (tau_final)
    , .tau_out      (tau_out)
    , .z1_out       (z1_out)
    , .ok           (okay)
    , .ready        (ready)
    , .fin_layer    (fin_layer)
    );

task randomize_vals;
    integer i;
begin
    tau_final = $f_rand();
    for (i = 0; i < nInBits; i = i + 1) begin
        w1[i] = $f_rand();
        w2[i] = $f_rand();
    end
    for (i = 0; i < nCopyBits; i = i + 1) begin
        w3[i] = $f_rand();
    end
end
endtask

endmodule
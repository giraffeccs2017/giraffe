// synthesis VERILOG_INPUT_VERSION SYSTEMVERILOG_2012
// testbench for prover_interpolate_qc

`include "prover_interpolate_qc.sv"

module test ();

reg clk, rstb, en, trig, cubic;
wire ready, ready_pulse;
reg [`F_NBITS-1:0] y_in [3:0];
wire [`F_NBITS-1:0] c_out [3:0];

prover_interpolate_qc iQC
    ( .clk              (clk)
    , .rstb             (rstb)
    , .en               (en | trig)
    , .cubic            (cubic)
    , .y_in             (y_in)
    , .c_out            (c_out)
    , .ready_pulse      (ready_pulse)
    , .ready            (ready)
    );

integer i, rseed;
initial begin
    $dumpfile("prover_interpolate_qc_test.fst");
    $dumpvars;
    for (i = 0; i < 4; i = i + 1) begin
        $dumpvars(0, y_in[i]);
        $dumpvars(0, c_out[i]);
    end
    rseed = 1;
    randomize_yi();
    clk = 0;
    rstb = 0;
    trig = 0;
    en = 0;
    cubic = 0;
    #1 rstb = 1;
    clk = 1;
    #1 trig = 1;
    #2 trig = 0;
    #1000 $finish;
end

`ALWAYS_FF @(posedge clk) begin
    en <= ready_pulse;
    if (ready_pulse) begin
        cubic <= ~cubic;
        check_outputs();
        randomize_yi();
    end
end

`ALWAYS_FF @(clk) begin
    clk <= #1 ~clk;
end

task check_outputs;
    integer i, j, max;
    reg [`F_NBITS-1:0] tmp, val;
begin
    max = cubic ? 4 : 3;
    $display("**");
    for (i = 0; i < max; i = i + 1) begin
        case (i)
            0: begin
                val = {(`F_NBITS){1'b0}};
            end

            1: begin
                val = {{(`F_NBITS-1){1'b0}},1'b1};
            end

            2: begin
                val = `F_M1;
            end

            3: begin
                val = {{(`F_NBITS-2){1'b0}}, 2'b10};
            end
        endcase
        tmp = c_out[max - 1];
        for (j = max - 2; j >= 0; j = j - 1) begin
            tmp = $f_mul(tmp, val);
            tmp = $f_add(tmp, c_out[j]);
        end
        $display("%h %h %s", tmp, y_in[i], tmp == y_in[i] ? ":)" : "!!");
    end
end
endtask

task randomize_yi;
    integer i;
    reg [`F_NBITS-1:0] tmp;
begin
    for (i = 0; i < 4; i = i + 1) begin
        tmp = $random(rseed);
        tmp = {tmp[31:0], 32'b0} | $random(rseed);
        y_in[i] = tmp;
    end
end
endtask

endmodule

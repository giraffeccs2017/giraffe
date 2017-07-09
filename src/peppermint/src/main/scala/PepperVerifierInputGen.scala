package peppermint.PepperVerifierInputGen

object PepperVerifierInputGen {
  def classNameToCPPCode(className : String, inputFileName : String) =
    """
      |#include <apps_sfdl_gen/%s_v_inp_gen.h>
      |#include <apps_sfdl_gen/%s_cons.h>
      |#pragma pack(push)
      |#pragma pack(1)
      |
      |//This file will NOT be overwritten by the code generator, if it already
      |//exists. make clean will also not remove this file.
      |
      |%sVerifierInpGen::%sVerifierInpGen() {
      |}
      |
      |void %sVerifierInpGen::create_input(mpq_t* input_q, int
      |num_inputs) {
      |#if IS_REDUCER == 0
      |
      |  //gmp_printf("Creating inputs\n");
      |    FILE *in = fopen("%s", "r");
      |
      |    for(int i=0; i < num_inputs; i++) {
      |        //v->get_random_signedint_vec(1, input_q + i, 32);
      |        char buffer[100];
      |        fscanf(in, "%%s", buffer);
      |        mpq_set_str(input_q[i], buffer, 0);
      |    }
      |    fclose(in);
      |
      |#endif
      |}
      |
      |#pragma pack(pop)
    """.stripMargin.format(className, className, className, className, className, inputFileName)

}

import run_giraffe_sv as g
import sys
import math


def paddzero(input, size):
    single = len(input) / size
    newInput = []
    for i in xrange(0, len(input), single):
        newInput.extend(input[i:(i+single)])
        newInput.append(0)
    return newInput

f = open("log", 'w')

def read_input(x,y,size):
    if size == 1:
        pass
    else:
        pass

def write_output(x,size):
    if size == 1:
        pass
    else:
        pass


def outsource_to_giraffe(pws, size, inputs):
    nCopyBits = int(math.log(size, 2))
    f.write("\n\nbegin\n")
    f.write(pws + "\n")
    f.write("copies: " + str(2**nCopyBits) + "\n")
    inputs = paddzero(inputs, 2**nCopyBits)
    g.ServerInfo.inputs = inputs
    g.ServerInfo.pws_file = pws
    g.ServerInfo.nCopyBits = nCopyBits
    g.ServerInfo.rundir = "../sim/icarus/"
    g.ServerInfo.muxsels = None
 #   g.ServerInfo.quiet = True
    f.write("Settings done\n")
    f.write("inputs size: " + str(len(inputs)) + "\n")
    f.flush()
    try:
        g.main()
    except:
        e = sys.exc_info()
        f.write("main crashed\n")
        f.write(str(e) + '\n')
        f.close()
        sys.exit()
    f.write("main finished\n")
    f.write("output: " + str(hash(tuple(g.ServerInfo.outputs))) + " " +
            str(len(g.ServerInfo.outputs)) + "\n")
    return tuple(g.ServerInfo.outputs)


if __name__ == "__main__":
    # nttopt.pws has 7 input per subcircuit
    for i in xrange(4, 5):
        f.write(str(i)+"\n")
        try:
            outsource_to_giraffe("820009343.pws", 2**i, range(0, 21 * (2**i)))
            #outsource_to_giraffe("nttopt.pws", 2**i, range(0, 6 * (2**i)))
        except:
            e = sys.exc_info()
            f.write("run crashed\n")
            f.write(str(e) + "\n")
            f.close()
            sys.exit()
    f.close()

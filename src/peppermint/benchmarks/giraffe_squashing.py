import giraffe_squashing_data_gen
import giraffe_squashing_plots
# driver of squashing plot

if __name__ == "__main__":
    logfile = "benchmarks/giraffe_squash_micro_data"
    giraffe_squashing_data_gen.generateData(logfile)
    giraffe_squashing_plots.main(logfile)

from string import Template
from subprocess import call

# try widths 2^4 to 2^12
logwidths_to_try = xrange(4,13)
total_width = 2**18
depth = 20

app_template = Template(r"""
#define W $width
#define D $depth
#define TOTALWIDTH $totalwidth

#define giraffe_squash_ac(x, C, W, p) \
         for (i = 0; i < C; i++) { \
                    for (k = 0; k < D; k++) { \
                      for (j = 0; j < p; j++) { \
                                       x[j] = x[j] * x[j]; \
                                   } \
                      for (j = p; j < W; j++) { \
                                       x[j] = x[j] + x[j]; \
                                   } \
                    }\
                  }

struct Input {
    int i;
};

struct Output {
    int y[W];
};

void compute(struct Input *input, struct Output *output) {
    int x[W];
    int i, j, k;
    giraffe_squash_ac(output->y, TOTALWIDTH/W, W, W/2);
}
""")


def generate_app_with_params(width):
    return app_template.substitute(width = str(width),
                                   depth = str(depth),
                                   totalwidth = str(total_width))


def write_content_to_file(content, fname):
    f = open(fname, "w")
    f.write(content)
    f.close()

def run_peppermint_on_file_width(fname, width, logfile):
    tag = "%s %s" % (str(width), str(total_width * depth / 2 * 71))
    call(["sbt", "run-main Divider %s GSquash %s %s" % (fname, logfile, tag)])

def generateData(logfile):
    call(["rm", "-f", logfile])
    call(["mkdir", "-p", "benchmarks/apps_tmp/"])
    for lw in logwidths_to_try:
        w = 2**lw
        app = generate_app_with_params(w)
        fname = "benchmarks/apps_tmp/giraffe_squashing_%s.c" % str(lw)
        write_content_to_file(app, fname)
        run_peppermint_on_file_width(fname, lw, logfile)

if __name__ == "__main__":
    generateData()

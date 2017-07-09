
def parse_file(fname):
    # returns a list of list of nums
    ret = []
    with open(fname) as f:
        for line in f:
            vec = line.split()
            if len(vec) != 0:
                ret.append(map(float, vec))
    return ret



#!/usr/bin/python

import matplotlib as mplib
mplib.use('Agg') # this removes X server error..
import matplotlib.pyplot as plt

def parse_outs(invals):
    xaxis = [ x[0] for x in invals ]
    yaxis = [ x[1] / x[2] for x in invals ]

    return (xaxis, yaxis)

def make_plots(invals):
    mplib.rc('text', usetex=True)
    mplib.rc('font', size=18)
    mplib.rc('figure', figsize=(7,5))

    marksize=9
    (b_xaxis, b_tots) = parse_outs(invals)

    plt.cla()
    (_, ax) = plt.subplots()
    plt.grid(True)
    plt.plot([0, 40], [1, 1], 'k--', linewidth=3)
    plt.semilogy(b_xaxis, b_tots, color='black', linestyle='solid', marker='<', markerfacecolor='cyan',
        label='keeping $C\cdot G$ constant', hold=True, markersize=marksize)
    plt.legend(loc='best', fontsize=16, fancybox=True)
    plt.axis([3.5, 13.5, 1.25, 22.5])
    xaxisrange = range(4,13)
    plt.xticks(xaxisrange)
    yticks = [1.5, 22]
    plt.yticks(yticks)
    ax.set_yticklabels([ str(x) for x in yticks ])
    ax.set_xticklabels([ str(x) for x in xaxisrange ])
    plt.ylabel("Performance relative to native execution\n(higher is better)")
    plt.xlabel("$\\log_{2} G$ (width)")
    plt.savefig('benchmarks/giraffe_squashing_micro.pdf', dpi=600, bbox_inches='tight')
    plt.close('all')

def main(fname):
    import benchmarks_util
    data = benchmarks_util.parse_file(fname)
    make_plots(data)

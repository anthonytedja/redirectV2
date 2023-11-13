set terminal pngcairo enhanced font 'arial,10' #size 800,600
method = system("echo $method")

set output method . ".render.png"

set title method . ' latency of requests (ms)'

set key off
set border 3

# Add a vertical dotted line at x=0 to show centre (mean) of distribution.
set yzeroaxis

# Each bar is half the (visual) width of its x-range.
set boxwidth 1 absolute
set style fill solid 1.0 noborder

bin_width = 2;

bin_number(x) = floor(x/bin_width)

set xlabel 'latency (ms)'
set ylabel 'frequency'

set logscale x 2

rounded(x) = bin_width * ( bin_number(x) + 0.5 )

filename = method . ".ALL.out"
plot filename using (rounded($1)):(1) smooth frequency with boxes
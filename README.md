<p align="center"><img src="https://raw.githubusercontent.com/erdos/erdos.stats/master/doc/logo.png" alt="erdos.stats logo"/></p>

# erdos.stats

Basic statistics utilities in Clojure.

## Usage

- First, add the dependency to Leiningen from <a href="https://clojars.org/erdos.stats">Clojars</a>: `[erdos.stats "0.1.0-SNAPSHOT"]`
- Fetch the dependencies with `lein deps`
- You need to require the namespace: `(require '[erdos.stats :as stats])`

The following functions are implemented. For usage, see the examples and the source code.

- sampling: `sample-with-probs`, `rand-samples`
- exploration: `hist`, `report`, `hist-print-ascii`
- operations: `sum`, `prod`
- stats: `mean`, `median`, `median-low`, `median-high`, `mode`, `variance`, `sd` (aka. `standard-deviation`), `skewness`, `kurtosis`

## Examples

### Averages

```clojure
(mean [1 2 2 3 4]) ; => 2.5
(mode [1 2 2 3 4]) ; => 2
(median [1 2 2 3 4]) ; => 2
```

### Quick histogram vector

You can get a general idea about the distribution of values in a collection using the `hist` function. Given a collection it returns a vector of buckets that represents a histogram.

```clojure
(stats/hist (repeatedly 1000 stats/normal))
;; => [1 9 25 54 74 134 157 174 150 90 67 43 13 5 3 1]
```

When given two arguments, the first argument is the number of buckets and the second argument is the collection.

```clojure
(stats/hist 3 (range 99))
;; => [33 33 33]
```

### Reporting

You can print basic statistics to the standard output.

In this example, we create a series of random numbers with Gaussian distribution.

```clojure
(def xs (repeatedly 10000 stats/normal))
```

Calling `(stats/report xs)` results something like the following:

```
--------------
Count:  10000
Min:    -4.15101725230182
Mean:   -0.008607583618416693
Max:    3.875746881304692
Median: -0.0038671443172906385
Mode:   -0.5172982636402658
Variance:   0.997574255434155
Deviation:  0.9987863912940319
Skewness:   -0.020849505804486506
Kurtosis:   3.0074920441970137
Histogram: 
┌────────────────────────────────────────────────┐
│                       ▖ ▐                      │
│                      ▄███▙▖▖                   │
│                    ████████▙▖                  │
│                   ▐█████████▙█                 │
│                 ▗██████████████                │
│                ▟███████████████▙▄              │
│              ▟▟██████████████████▙▖            │
│          ▗▗▄████████████████████████▄▄         │
└────────────────────────────────────────────────┘
```


## License

Copyright © 2016 Janos Erdos

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.



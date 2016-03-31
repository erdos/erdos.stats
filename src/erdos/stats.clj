(ns erdos.stats
  "Basic statistic utilities

  Sources
  [1] http://www.itl.nist.gov/div898/handbook/eda/section3/eda35b.htm
  [2] https://docs.racket-lang.org/math/stats.html
")

(defn samples-with-probs [kps]
  (assert (map? kps))
  (assert (every? float? (vals kps)))
  (let [sum (reduce + (vals kps))
        vs  (for [v (vals kps)] (/ v sum))
        intervals (reductions + 0 vs)
        tree      (apply sorted-map (interleave intervals (keys kps)))]
    (repeatedly #(val (first (.seqFrom tree (rand) false))))))

(comment
  (let [dist {:a 0.2 :b 0.1 :c 0.7}
        n 10000
        samples (take n (samples-with-probs dist))]
    (time (dorun samples))
    (frequencies samples))
;; => {:c 7011, :a 1971, :b 1018}
  )


(defn rand-samples
  "Uniformly random samples"
  ([n xs] (repeatedly n (partial rand-nth (vec xs))))
  ([xs] (repeatedly (partial rand-nth (vec xs)))))

(defn normal
  "Random value in Gaussian normal distribution"
  ([^double m ^double d2]
   (+ m (* (Math/sqrt d2) (normal))))
  ([]
   (* (Math/sqrt (* -2.0 (Math/log (rand))))
      (Math/cos (* 2.0 Math/PI (rand))))))

(defn mean [xs]
  (when (seq xs)
    (/ (reduce + 0.0 xs) (count xs))))

(defn sum [xs] (reduce + 0.0 xs))
(defn prod [xs] (reduce * 1.0 xs))

(defn median [xs]
  (when (seq xs)
    (let [xs (vec (sort xs))
          |xs| (count xs)]
      (if (odd? |xs|)
        (nth xs (/ |xs| 2))
        (/ (double
            (+ (nth xs (quot |xs| 2))
               (nth xs (dec (quot |xs| 2)))))
           2)))))

(defn median-low [xs]
  (when (seq xs)
    (let [xs (vec (sort xs))
          |xs| (count xs)]
      (if (odd? |xs|)
        (nth xs (/ |xs| 2))
        (min (nth xs (quot |xs| 2))
             (nth xs (dec (quot |xs| 2))))))))

(defn median-high [xs]
  (when (seq xs)
    (let [xs (vec (sort xs))
          |xs| (count xs)]
      (if (odd? |xs|)
        (nth xs (/ |xs| 2))
        (max (nth xs (quot |xs| 2))
             (nth xs (dec (quot |xs| 2))))))))

(defn mode [xs]
  (when (seq xs)
    (apply max-key (frequencies xs) xs)))

(defmacro square [x] `(let [x# ~x] (* x# x#)))

(defmacro cube [x] `(Math/pow ~x 3))

(defn variance
  "Variance = average of squared differences from the mean"
  [xs]
  (let [mxs (mean xs)]
    (mean (for [x xs] (square (- mxs x))))))

(defn sd
  "Standard Deviation = square root of variance"
  [xs] (Math/sqrt (variance xs)))

(def standard-deviation sd)

(defn skewness [xs]
  (let [m (mean xs)
         s (sd xs)]
    (/ (mean (for [x xs] (cube (- x m))))
       (cube s))))

(defn kurtosis [xs]
  (let [m (mean xs)
         s (sd xs)]
    (/ (mean (for [x xs] (Math/pow (- x m) 4)))
       (Math/pow s 4))))

                                        ; REPORTING

(defn hist
  ([xs] (hist (min 16 (count xs)) xs))
  ([n xs]
   (assert (and (integer? n) (pos? n))
           "First arg must be a positive integer.")
   (assert (and (coll? xs) (seq xs))
           "Second arg must be a nonempty coll.")
   (let [m- (apply min xs)
         m+ (apply max xs)
         d  (double (- m+ m-))
         w  (/ d n)
         f  (fn [x] (int (quot (- x m- 0.0000001) w)))
         gs (group-by f xs)]
     (mapv (comp count gs) (range 0 n)))))

;; (hist 2 (repeatedly 100000 normal))
;; (hist 3 (range 99))


(defn hist-print-ascii [xs]
  (let [ph 18 pw 100
        _ (assert (even? pw))
        _ (assert (even? ph))
        h (hist pw xs)
        m+ (apply max h)
        m- 0;(apply min h)
        w  (- m+ m-)
        h' (vec (for [x h] (-> x (- m-) (/ w) (* ph) int)))
        f (fn [col row] (if (< (h' col) row) 0 1))
        m (vec " ???▖▌??▗?▐?▄▙▟█")
        ]
    (println (str "┌" (apply str (repeat (quot pw 2) "─")) "┐"))
    (doseq [row (range ph 0 -2)]
      (print "│")
      (doseq [col (range 0 pw 2)
              :let [a (f col row)
                    b (f (inc col) row)
                    c (f col (dec row))
                    d (f (inc col) (dec row))]]
        (print (m (+ (* 1 a) (* 2 b) (* 4 c) (* 8 d)) )))
      (println "│"))
    (println (str "└" (apply str (repeat (quot pw 2) "─")) "┘"))))

;EXAMPLE: (hist-print-ascii (repeatedly 80000 normal))

(defn report [xs]
  (println "--------------")
  (println "Count:" \tab (count xs))
  (println "Min:" \tab (apply min xs))
  (println "Mean:" \tab (mean xs))
  (println "Max:" \tab (apply max xs))
  (println "Median:\t" (median xs))
  (println "Mode:" \tab (mode xs))
  (println "Variance:" \tab (variance xs))
  (println "Deviation:" \tab (sd xs))
  (println "Skewness:" \tab (skewness xs))
  (println "Kurtosis:" \tab (kurtosis xs))
  (println "Histogram:") (hist-print-ascii xs)
  (println "--------------"))

; EXAMPLE:
;; (report (repeatedly 10000 normal))

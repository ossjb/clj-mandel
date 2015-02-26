(ns mandel.core
  (:import (java.awt Canvas Color Dimension Graphics)
           (java.awt.image BufferedImage BufferStrategy)
           (javax.swing JFrame))
  (:gen-class))

;(set! *warn-on-reflection* true)

(defn mandelbrot
  [^double cx ^double cy ^long max-iterations]
  (let [log2 (Math/log 2)
        log4 (Math/log 4)]
    (loop [x 0
           y 0
           iter 0]
      (let [abs-squared (+ (* x x) (* y y))]
        (if (or (> abs-squared 8) (= iter max-iterations))
          (- iter (/ (Math/log (/ (Math/log abs-squared) log4)) log2))
          (recur (+ (- (* x x) (* y y)) cx)
                 (+ (* 2 x y) cy)
                 (unchecked-inc iter)))))))

(defn hue-to-rgb ^long
  [^long hue]
  (let [hue (mod hue 360)
        x (long (unchecked-multiply (unchecked-subtract 1.0 (Math/abs ^double (unchecked-dec (mod (/ hue 60.0) 2.0)))) 0xff))]
    (cond
      (< hue 60) (unchecked-add-int 0xff0000 (* 0x100 x))
      (< hue 120) (unchecked-add-int (* 0x10000 x) 0xff00)
      (< hue 180) (unchecked-add-int 0xff00 x)
      (< hue 240) (unchecked-add-int (* 0x100 x) 0xff)
      (< hue 300) (unchecked-add-int (* 0x10000 x) 0xff)
      (< hue 360) (unchecked-add-int 0xff0000 x))))

(defn get-color ^long
  [^long x ^long y ^long width ^long height]
  (let [cx (* 4 (/ (- x (/ width 1.5)) width))
        cy (* 4 (/ (- y (/ height 2)) height))
        max-iterations 100
        iter (mandelbrot cx cy max-iterations)]
    (if (>= iter max-iterations)
      0x000000
      (hue-to-rgb (unchecked-add-int 240 (unchecked-divide-int (unchecked-multiply-int iter 360) max-iterations))))))

(defn set-pixel
  [image-data width x y color]
  (aset-int image-data (unchecked-add-int (unchecked-multiply-int y width) x) color))

(defn draw-line
  [image-data width height y]
  (doseq [x (range width)]
    (set-pixel image-data width x y (get-color x y width height))))

(defn draw
  [frame buffered-image width height]
  (let [image-data (-> buffered-image (.getRaster) (.getDataBuffer) (.getData))]
    (dorun (pmap #(draw-line image-data width height %) (shuffle (range height)))))
  (.repaint frame))

(defn create-frame
  [buffered-image]
  (let [frame (proxy [JFrame] []
                (paint
                  [g]
                  (.drawImage g buffered-image 0 0 this)))]
    (doto frame
      (.setSize (Dimension. (.getWidth buffered-image) (.getHeight buffered-image)))
      (.show))
    frame))

(defn mandel
  [width height]
  (let [buffered-image (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        frame (create-frame buffered-image)]

    (time (draw frame buffered-image width height))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (mandel 800 800))



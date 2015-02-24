(ns mandel.core
  (:import (java.awt Canvas Color Dimension Graphics)
           (java.awt.image BufferedImage BufferStrategy)
           (javax.swing JFrame))
  (:gen-class))

(set! *warn-on-reflection* true)

(defn hue-to-rgb ^long
  [^long hue]
  (let [hue (mod hue 360)
        x (long (* (- 1.0 (Math/abs ^double (dec (mod (/ hue 60.0) 2.0)))) 0xff))]
    (cond
      (< hue 60) (+ 0xff0000 (* 0x100 x))
      (< hue 120) (+ (* 0x10000 x) 0xff00)
      (< hue 180) (+ 0xff00 x)
      (< hue 240) (+ (* 0x100 x) 0xff)
      (< hue 300) (+ (* 0x10000 x) 0xff)
      (< hue 360) (+ 0xff0000 x))))

(defn get-color ^long
  [^long x ^long y ^long width ^long height]
  (hue-to-rgb (/ (* 360 x) width)))

(defn set-pixel
  [image-data width x y color]
  (aset-int image-data (+ (* y width) x) color))

(defn draw
  [buffered-image width height]
  (binding [*unchecked-math* true]
    (let [image-data (-> buffered-image (.getRaster) (.getDataBuffer) (.getData))]
      (doseq [y (range height)
              x (range width)]
        (set-pixel image-data width x y (get-color x y width height))))))

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

    (time (draw buffered-image width height))
    (.repaint frame)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (mandel 400 400))



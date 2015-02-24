(ns mandel.core
  (:import (java.awt Canvas Color Dimension Graphics)
           (java.awt.image BufferedImage BufferStrategy)
           (javax.swing JFrame))
  (:gen-class))

(set! *warn-on-reflection* true)

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
  [^long x ^long y ^long width]
  (hue-to-rgb (unchecked-divide-int (unchecked-multiply-int 360 x) width)))

(defn set-pixel
  [image-data width x y color]
  (aset-int image-data (unchecked-add-int (unchecked-multiply-int y width) x) color))

(defn draw-part
  [image-data width from-y to-y]
  (doseq [y (range from-y (inc to-y))
          x (range width)]
    (set-pixel image-data width x y (get-color x y width)))
  )
(defn draw
  [frame buffered-image width height]
  (let [image-data (-> buffered-image (.getRaster) (.getDataBuffer) (.getData))]
    (dorun (pmap #(draw-part image-data width % %) (shuffle (range height)))))
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
  (mandel 400 400))



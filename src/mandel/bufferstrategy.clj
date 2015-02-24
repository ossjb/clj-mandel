(ns mandel.core
  (:import (java.awt Canvas Color Dimension Graphics)
           (java.awt.image BufferedImage BufferStrategy)
           (javax.swing JFrame))
  (:gen-class))

(defn paint
  [g width height]
  (doseq [y (range height)
          x (range width)]
    (.setColor g (Color.  (int (/ (* x 255) width)) (int (/ (* y 255) height)) 0))
    (.drawLine g x y x y)))

(defn draw
  [width height]
  (let [frame (proxy [JFrame]
                  []
                (paint
                  [g]
                  (let [strategy (.getBufferStrategy this)
                        g (.getDrawGraphics strategy)]
                    (paint g width height)
                    (.dispose g)
                    (.show strategy))))]
    (doto frame
      (.setSize (Dimension. width height))
      (.show)
      (.createBufferStrategy 2))
    frame))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (draw 400 400))



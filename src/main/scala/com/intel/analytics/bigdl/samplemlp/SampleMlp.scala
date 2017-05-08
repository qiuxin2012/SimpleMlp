package com.intel.analytics.bigdl.samplemlp

import com.intel.analytics.bigdl.dataset.{DataSet, MiniBatch, Sample, SampleToBatch}
import com.intel.analytics.bigdl._
import com.intel.analytics.bigdl.nn._
import com.intel.analytics.bigdl.optim._
import com.intel.analytics.bigdl.tensor.Tensor
import com.intel.analytics.bigdl.utils.{Engine, T}
import org.apache.spark.SparkContext
import com.intel.analytics.bigdl.tensor.TensorNumericMath.TensorNumeric._
import Utils._
import com.intel.analytics.bigdl.visualization.{TrainSummary, ValidationSummary}
import org.apache.log4j.{Level, Logger}

object SampleMlp {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)
  Logger.getLogger("breeze").setLevel(Level.ERROR)
  Logger.getLogger("com.intel.analytics").setLevel(Level.INFO)

  def main(args: Array[String]): Unit = {
    testParser.parse(args, new TestParams()).map(param => {
      val dimInput = param.dimInput
      val nHidden = param.nHidden
      val recordSize = param.recordSize
      val maxEpoch = param.maxEpoch
      val batchSize = param.batchSize

      val sc = new SparkContext(
        Engine.createSparkConf()
          .setAppName(s"SampleMlp-$dimInput-$nHidden-$recordSize-$maxEpoch-$batchSize")
          .set("spark.task.maxFailures", "1")
          .setMaster("local[1]")
      )
      Engine.init(1, 1, true)

      // make up some data
      val data = sc.range(0, recordSize, 1).map { _ =>
        val featureTensor = Tensor[Double](dimInput)
        featureTensor.apply1(_ => scala.util.Random.nextFloat())
        val labelTensor = Tensor[Double](1)
        labelTensor(Array(1)) = Math.round(scala.util.Random.nextFloat())
        new Sample[Double](featureTensor, labelTensor)
      }

      val trainSet = DataSet.rdd(data).transform(SampleToBatch[Double](batchSize))

      val layer1 = Linear[Double](dimInput, nHidden)
      val layer2 = ReLU[Double]()
      val layer3 = Linear[Double](nHidden, nHidden)
      val layer4 = ReLU[Double]()
      val output = Linear[Double](nHidden, 1)
      val model = Sequential[Double]().
        add(Reshape(Array(dimInput))).
        add(layer1).
        add(layer2).
        add(layer3).
        add(layer4).
        add(output)

      println(model)

      val state = T(
          "learningRate" -> 0.01
        )
      val criterion = MSECriterion[Double]()

      val optimizer = Optimizer[Double, MiniBatch[Double]](model, trainSet, criterion)

      val trainSummary = TrainSummary(".", "sampleMlp")

      optimizer.
        setTrainSummary(trainSummary).
        setState(state).
        setEndWhen(Trigger.maxEpoch(maxEpoch)).
        setOptimMethod(new Adagrad[Double]()).
        optimize()
    })
  }
}

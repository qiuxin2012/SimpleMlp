package com.intel.analytics.bigdl.samplemlp

import com.intel.analytics.bigdl.dataset.{DataSet, MiniBatch, Sample, SampleToBatch}
import com.intel.analytics.bigdl._
import com.intel.analytics.bigdl.nn._
import com.intel.analytics.bigdl.optim._
import com.intel.analytics.bigdl.tensor.Tensor
import com.intel.analytics.bigdl.utils.{Engine, T}
import org.apache.spark.SparkContext
import com.intel.analytics.bigdl.tensor.TensorNumericMath.TensorNumeric._

object SampleMlp {
  def main(args: Array[String]): Unit = {
    val dimInput = 70
    val nHidden = 100
    val recordSize = 1e7.toLong
    val maxEpoch = 10
    val coreNum = 28
    val nodeNum = 16
    val batchSize = coreNum * nodeNum * 10

    val sc = new SparkContext(
      Engine.init(16, 28, true).get
        .setAppName("SampleMlp")
        .set("spark.task.maxFailures", "1")
    )

    // make up some data
    val data = sc.range(0, recordSize, 1, coreNum * nodeNum).map{_ =>
      val featureTensor = Tensor[Double](dimInput)
      featureTensor.apply1(_ => scala.util.Random.nextFloat())
      val labelTensor = Tensor[Double](1)
      labelTensor(Array(1)) = Math.round(scala.util.Random.nextFloat())
      new Sample[Double](featureTensor, labelTensor)
    }

    val trainSet = DataSet.rdd(data).transform(SampleToBatch[Double](batchSize))

    val layer1 = Linear[Double](dimInput,nHidden)
    val layer2 = ReLU[Double]()
    val layer3 = Linear[Double](nHidden,nHidden)
    val layer4 = ReLU[Double]()
    val output =  Linear[Double](nHidden,1)
    val model = Sequential[Double]().
      add(Reshape(Array(dimInput))).
      add(layer1).
      add(layer2).
      add(layer3).
      add(layer4).
      add(output)

    val state =
      T(
        "learningRate" -> 0.01
      )
    val criterion = MSECriterion[Double]()

    val optimizer = Optimizer[Double, MiniBatch[Double]](model, trainSet, criterion)

    optimizer.
      setState(state).
      setEndWhen(Trigger.maxEpoch(maxEpoch)).
      setOptimMethod(new Adagrad[Double]()).
      optimize()
  }

}

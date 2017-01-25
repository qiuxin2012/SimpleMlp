package com.intel.analytics.bigdl.samplemlp

import scopt.OptionParser

object Utils {
  case class TestParams(
                         dimInput: Int = 70,
                         nHidden: Int = 100,
                         recordSize: Long = 1e7.toLong,
                         maxEpoch: Int = 10,
                         coreNum: Int = 28,
                         nodeNum: Int = 8,
                         batchSize: Int = 448
                       )

  val testParser = new OptionParser[TestParams]("BigDL Lenet Test Example") {
    opt[Int]('d', "dimInput")
      .text("dimension of input")
      .action((x, c) => c.copy(dimInput = x))
      .required()
    opt[Int]('h', "nHidden")
      .text("Num of hidden layer")
      .action((x, c) => c.copy(nHidden = x))
      .required()
    opt[Double]('r', "recordSize")
      .text("Total record size")
      .action((x, c) => c.copy(recordSize = x.toLong))
      .required()
    opt[Int]('e', "maxEpoch")
      .text("maxEpoch")
      .action((x, c) => c.copy(maxEpoch = x))
      .required()
    opt[Int]('c', "core")
      .text("cores number on each node")
      .action((x, c) => c.copy(coreNum = x))
      .required()
    opt[Int]('n', "nodeNumber")
      .text("nodes number to train the model")
      .action((x, c) => c.copy(nodeNum = x))
      .required()
    opt[Int]('b', "batchSize")
      .text("batch size")
      .action((x, c) => c.copy(batchSize = x))
      .required()
  }

}
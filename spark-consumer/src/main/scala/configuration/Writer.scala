package configuration

import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Row}

class Writer(config: DestinationConfig) {

  def prepareRun(dataFrame: DataFrame): DataStreamWriter[Row] = {
    val optionsMap = config.getOptionsMap
    val baseWriter = dataFrame.writeStream
      .outputMode(config.getOutputMode)
      .format(config.getFormat)
    optionsMap match {
      case null =>  baseWriter
      case _ => baseWriter.options(optionsMap)
    }

  }
}

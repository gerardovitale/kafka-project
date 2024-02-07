package configuration

import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.streaming.DataStreamWriter

class Writer(config: DestinationConfig) {

  def prepareRun(dataFrame: DataFrame): DataStreamWriter[Row] = {
    dataFrame.writeStream
      .outputMode(config.getOutputMode)
      .format(config.getFormat)
      .options(config.getOptionsMap)
  }
}

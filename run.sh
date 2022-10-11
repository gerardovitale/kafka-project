source ./.env


dateTimeNow() {
  date +%m/%d/%YT%H:%M:%S
}


logger() {
  echo "$(dateTimeNow) - [$1] - $2"
}


getKafkaClusterId() {
  cluster_id=$(curl -s "$KAFKA_REST_PROXY_SERVER"/v3/clusters/ | 
    grep -oE '"cluster_id":"(.*?)"' |
    sed -e 's/^"cluster_id":"//' -e 's/"$//')
  logger "INFO" "got kafka cluster id: $cluster_id"
}


createTopicByClusterId() {
  topic_res=$(curl -sw "%{http_code}" "$KAFKA_REST_PROXY_SERVER"/topics/"$KAFKA_TOPIC" | tail -c 3)

  if [[ $topic_res != 200 ]]; then
    topic_created=$(curl -sw "%{http_code}" \
      --header "Content-Type: application/json" \
      --request POST \
      --data "{\"topic_name\":\"$KAFKA_TOPIC\",\"partitions_count\":1,\"replication_factor\": 1,\"configs\":[]}" \
      "$KAFKA_REST_PROXY_SERVER"/v3/clusters/"$1"/topics |
      tail -c 3)

    if [[ $topic_created == 201 ]]; then
      logger "INFO" "topic $KAFKA_TOPIC created"
    else
      logger "ERROR" "topic $KAFKA_TOPIC wasn't created"
      exit 1
    fi

  elif [[ $topic_res == 200 ]]; then
    logger "WARN" "topic already exists: $KAFKA_TOPIC"

  else
    logger "ERROR" "topic error code $topic_res"
    exit 1
  fi
}


createKafkaTopics() {
  logger "INFO" "kafka-rest-proxy health-check"
  rest_proxy_health_check=$(curl -sw "%{http_code}" "$KAFKA_REST_PROXY_SERVER" | tail -c 3)

  if [[ $rest_proxy_health_check != 200 ]]; then
    logger "INFO" "waiting for kafka-rest-proxy..."
    sleep 10
    createKafkaTopics

  elif [[ $rest_proxy_health_check == 200 ]]; then
    getKafkaClusterId
    createTopicByClusterId $cluster_id

  else
    logger "ERROR" "rest_proxy_health_check error code: $rest_proxy_health_check"
    exit 1
  fi

}


createKafkaTopics

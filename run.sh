source ./.env

startDockerEnv() {
  if [[ $1 == prune ]]; then
    docker-compose stop
    docker container prune
  fi
  docker-compose up -d || exit 1
}

createKafkaTopics() {
  echo "[INFO] kafka-rest-proxy health-check"
  restProxyHealthCheck=$(curl -sw "%{http_code}" "$KAFKA_REST_PROXY_SERVER" | tail -c 3)

  if [[ $restProxyHealthCheck != 200 ]]; then
    echo "[INFO] waiting for kafka-rest-proxy..."
    sleep 10
    createKafkaTopics

  elif [[ $restProxyHealthCheck == 200 ]]; then
    cluster_id=$(curl -s "$KAFKA_REST_PROXY_SERVER"/v3/clusters/ | grep -oE '"cluster_id":"(.*?)"' |
      sed -e 's/^"cluster_id":"//' -e 's/"$//')
    echo "[INFO] getting kafka cluster id: $cluster_id"

    topic_res=$(curl -sw "%{http_code}" "$KAFKA_REST_PROXY_SERVER"/topics/"$KAFKA_TOPIC" | tail -c 3)

    if [[ $topic_res != 200 ]]; then
      echo "[INFO] creating kafka topic: $KAFKA_TOPIC"
      curl --header "Content-Type: application/json" \
        --request POST \
        --data "{\"topic_name\":\"$KAFKA_TOPIC\",\"partitions_count\":1,\"replication_factor\": 1,\"configs\":[]}" \
        -s "$KAFKA_REST_PROXY_SERVER"/v3/clusters/"$cluster_id"/topics

    elif [[ $topic_res == 200 ]]; then
      echo "[INFO] topic already exists: $KAFKA_TOPIC"

    else
      echo "topic error code $topic_res"
      exit 1
    fi

  else
    echo "restProxyHealthCheck error code $restProxyHealthCheck"
    exit 1
  fi

}

startDockerEnv "$@"
createKafkaTopics

source ./.env

startDockerEnv() {
  if [[ $1 == prune ]]; then
    docker-compose stop || exit 1
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
    echo "[INFO] got kafka cluster id: $cluster_id"

    topic_res=$(curl -sw "%{http_code}" "$KAFKA_REST_PROXY_SERVER"/topics/"$KAFKA_TOPIC" | tail -c 3)

    if [[ $topic_res != 200 ]]; then
      topicCreated=$(curl -sw "%{http_code}" \
        --header "Content-Type: application/json" \
        --request POST \
        --data "{\"topic_name\":\"$KAFKA_TOPIC\",\"partitions_count\":1,\"replication_factor\": 1,\"configs\":[]}" \
        "$KAFKA_REST_PROXY_SERVER"/v3/clusters/"$cluster_id"/topics |
        tail -c 3)

        if [[ $topicCreated == 201 ]]; then
          echo "[INFO] topic $KAFKA_TOPIC created"
        else
          echo "[ERROR] topic $KAFKA_TOPIC wasn't created"
        fi

    elif [[ $topic_res == 200 ]]; then
      echo "[WARN] topic already exists: $KAFKA_TOPIC"

    else
      echo "[ERROR] topic error code $topic_res"
      exit 1
    fi

  else
    echo "[ERROR] restProxyHealthCheck error code $restProxyHealthCheck"
    exit 1
  fi

}

startDockerEnv "$@"
createKafkaTopics

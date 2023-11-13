#!/bin/bash 

TestLoad() {
    concurrent_connections=$1
    requests_per_connection=$2
    method=$3

    for connection in $(seq 1 $concurrent_connections); do
        java LoadTest.java 127.0.0.1 8000 $connection $method $requests_per_connection "$method.$connection.out" &
    done

    wait $(jobs -p)
}

TestLoad 4 400 GET 
TestLoad 4 400 PUT
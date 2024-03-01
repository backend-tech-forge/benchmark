http benchmarker
======

* erd: [https://www.erdcloud.com/d/MLpTGsonrqSK7ycAh](https://www.erdcloud.com/d/MLpTGsonrqSK7ycAh)
![image](https://github.com/backend-tech-forge/benchmark/assets/29156882/d03e745e-4217-4871-82d2-20e933ce439a)


* API usage

### `POST /api/benchmark`
#### Request

```json
{
  "url": "http://example.com:8080/user",
  "method": "POST",
  "headers": {
    "Content-Type": "application/json"
  },
  "body": {
    "name": "John Doe",
    "age": 25,
    ...
  },
  "prepare": {
    "url": "http://example.com:8080/login",
    "method": "POST",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "id": "...",
      "password": "..."
    }
  }
}
```

#### Response 200
```json
{
  "test_id": 26,
  "started_at": "2024-02-27T21:30:21.618101+09:00",
  "finished_at": "2024-02-27T21:30:21.618101+09:00",
  "url": "http://example.com:8080/user",
  "method": "POST",
  "total_requests": 10000,
  "total_errors": 0,
  "total_success": 10000,
  "StatusCodeCount": {
    "200": 10000,
    ...
  },
  "total_users": 10,
  "total_duration": "13s",
  "mttfb_average": "28.188ms",
  "MTTFBPercentiles": {
    "p50": "13.386ms",
    "p75": "23.908ms",
    "p90": "49.640ms",
    "p95": "103.998ms",
    "p99": "224.680ms"
  },
  "tps_average": 588.80,
  "TPSPercentiles": {
    "p50": 312.23,
    "p75": 113.19,
    "p90": 54.70,
    "p95": 23.55,
    "p99": 17.43
  }
}
```

#### Response 4xx
```json
{
  "test_id": 26,
  "error_code": "PREPARE_FAILED",
  "error_message": "Failed to prepare the test",
  "error_message_detail": "id or password is invalid"
}
```

#### Response 5xx
```json
{
  "test_id": 26,
  "error_code": "INTERNAL_SERVER_ERROR",
  "error_message": "",
  "error_message_detail": ""
}
```

### `GET /api/benchmark/results/{test_id}`
#### Response 200
```json
{
  "test_id": 26,
  "started_at": "2024-02-27T21:30:21.618101+09:00",
  "finished_at": "2024-02-27T21:30:21.618101+09:00",
  "url": "http://example.com:8080/user",
  "method": "POST",
  "total_requests": 10000,
  "total_errors": 0,
  "total_success": 10000,
  "StatusCodeCount": {
    "200": 10000,
    ...
  },
  "total_users": 10,
  "total_duration": "13s",
  "mttfb_average": "28.188ms",
  "MTTFBPercentiles": {
    "p50": "13.386ms",
    "p75": "23.908ms",
    "p90": "49.640ms",
    "p95": "103.998ms",
    "p99": "224.680ms"
  },
  "tps_average": 588.80,
  "TPSPercentiles": {
    "p50": 312.23,
    "p75": 113.19,
    "p90": 54.70,
    "p95": 23.55,
    "p99": 17.43
  }
}
```

#### Response 4xx
```json
{
  "test_id": 26,
  "error_code": "TEST_NOT_FOUND",
  "error_message": "Test not found",
  "error_message_detail": ""
}
```

#### Response 5xx
```json
{
  "test_id": 26,
  "error_code": "INTERNAL_SERVER_ERROR",
  "error_message": "",
  "error_message_detail": ""
}
```

### `GET /api/benchmark/results`

#### Response 200
```json
{
  "results": [
    {
      "test_id": 26,
      "started_at": "2024-02-27T21:30:21.618101+09:00",
      "finished_at": "2024-02-27T21:30:21.618101+09:00",
      "url": "http://example.com:8080/user",
      "method": "POST",
      "total_requests": 10000,
      "total_errors": 0,
      "total_success": 10000,
      "StatusCodeCount": {
        "200": 10000,
        ...
      },
      "total_users": 10,
      "total_duration": "13s",
      "mttfb_average": "28.188ms",
      "tps_average": 588.80,
    },
    {
      "test_id": 25,
      ...
    }
  ]
}
```

#### Response 5xx
```json
{
  "error_code": "INTERNAL_SERVER_ERROR",
  "error_message": "",
  "error_message_detail": ""
}
```


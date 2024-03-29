http benchmarker
======

* Java version : [amazon corrretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
* Spring boot version : 3.2.3

This is a simple http benchmark tool that can be used to **test the performance of a server**.

* erd link : [https://www.erdcloud.com/d/MLpTGsonrqSK7ycAh](https://www.erdcloud.com/d/MLpTGsonrqSK7ycAh)

<details>
<summary>ERD</summary>

![img.png](erd.png)

</details>



## API design


| Method | URL                            | Description                                         | Role |
|--------|--------------------------------|-----------------------------------------------------| --- |
| POST   | /api/user                      | Create a user                                       | ADMIN / USER |
| GET    | /api/user                      | Get the user information                            | ADMIN / USER |
| PUT    | /api/user                      | Update the user information                         | ADMIN / USER |
| POST   | /api/group                     | Create a group                                      | ADMIN |
| GET    | /api/groups                    | Get the list of groups                              | ADMIN |
| GET    | /api/group/{group_id}          | Get the group information                           | ADMIN |
| POST   | /api/template                  | Create a template                                   | ADMIN / USER |
| GET    | /api/templates                 | Get the list of template                            | ADMIN / USER |
| GET    | /api/template/{template_id}    | Get the template information                        | ADMIN / USER |
| PATCH  | /api/template/{template_id}    | Update a template                                   | ADMIN / USER |
| DELETE | /api/template/{template_id}    | Delete a template                                   | ADMIN / USER |
| POST   | /api/benchmark                 | Run a benchmark test                                | ADMIN / USER |
| GET    | /api/benchmark/result/{test_id} | Get the result of a benchmark test                  | ADMIN / USER |
| GET    | /api/benchmark/results         | Get the list of benchmark test results within group | ADMIN / USER |
| POST   | /login                         | Login                                               | ADMIN / USER |
| POST   | /logout                        | Logout                                              | ADMIN / USER |


* User roles
  * ADMIN : Can access all APIs
  * USER : Can access all APIs except for 
    * `POST /user/group`
    * `GET /user/groups` 
    * `GET /user/groups/{group_id}`

## API specification
### `POST /api/user` [ADMIN / USER]
#### Request

```json
{
  "id": "gyumin",
  "pw": "1234",
  "slack_webhook_url": "https://hooks.slack.com/services/...",
  "email": "ghkdqhrbals@gmail.com",
  "email_notification": true,
  "slack_notification": true
}
```

### `GET /api/user`
#### Response 200
```json
{
  "id": "gyumin",
  "slack_webhook_url": "https://hooks.slack.com/services/...",
  "email": "ghkdqhrbals@gmail.com",
  "email_notification": true,
  "slack_notification": true,
  "created_at": "2024-02-27T21:30:21.618101+09:00",
  "updated_at": "2024-02-27T21:30:21.618101+09:00"
}
```

#### Response 4xx
```json
{
  "error_code": "USER_NOT_FOUND",
  "error_message": "User not found",
  "error_message_detail": ""
}
```

### `PUT /api/user` [ADMIN / USER]

#### Request

```json
{
  "id": "gyumin",
  "group_id": "group-a", // error when write admin 
  "slack_webhook_url": "https://hooks.slack.com/services/...", 
  "email": "ghkdqhrbals@gmail.com", 
  "email_notification": true, 
  "slack_notification": true
}
```

### `POST /api/group` [ADMIN / USER]

#### Request

```json
{
  "group_id": "group-a",
  "description": "group A test reports"
}
```

### `GET /api/groups` [ADMIN]

#### Response

```json

{
  "groups": [
    {
      "group_id": "group-a",
      "description": "group A test reports",
      "created_at": "2024-02-27T21:30:21.618101+09:00",
      "users": [ "gyumin", "user1", "user2", ... ]
    },
    {
      "group_id": "group-b",
      "description": "group B test reports",
      "created_at": "2024-02-27T21:30:21.618101+09:00",
      "users": [ "user3", "user4", "user5", ... ]
    }
  ]
}

```

### `GET /api/group/{group_id}` [ADMIN / USER]

#### Response

```json
{
  "group_id": "group-a",
  "description": "group A test reports",
  "created_at": "2024-02-27T21:30:21.618101+09:00",
  "users": [ "gyumin", "user1", "user2", ... ]
}
```




### `POST /api/benchmark` [ADMIN / USER]
#### Request

```json
{
  "url": "http://example.com:8080/api/board",
  "method": "POST",
  "headers": {
    "Content-Type": "application/json"
  },
  "vuser":10,
  "request_per_user":1000,
  "body": {
    "board_title": "...",
    "board_content": "...",
    "user_id": "..."
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

### `GET /api/benchmark/result/{test_id}` [ADMIN / USER]
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

### `GET /api/benchmark/results` [ADMIN / USER]

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


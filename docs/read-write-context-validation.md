# Read/write context validation

OpenAPI schemas can mark properties as `readOnly` or `writeOnly` to indicate the direction in which they are used.
Kappa validates request bodies in the **write context** and response bodies in the **read context**:

| Property marker | Request body (write context) | Response body (read context) |
| --- | --- | --- |
| `readOnly: true` | The property must not be present | The property is allowed |
| `writeOnly: true` | The property is allowed | The property must not be present |

For example, with this schema, `id` is returned by the API but should not be sent by clients, while `password` can
be sent to the API but should not be returned:

```yaml
User:
  type: object
  properties:
    id:
      type: integer
      readOnly: true
    name:
      type: string
    password:
      type: string
      writeOnly: true
```

A request body such as `{"id": 1, "name": "John Doe", "password": "secret"}` fails validation because it includes
the read-only `id` property. A response containing `password` similarly fails because that property is write-only.

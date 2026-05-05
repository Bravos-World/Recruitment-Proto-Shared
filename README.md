# Proto Model — Recruitment Platform

Shared Protobuf / gRPC contract definitions for the **Bravos Recruitment Platform**.

This library contains `.proto` files and their generated Java classes used for inter-service communication via gRPC. It is published as a Java library so that both server-side services and infrastructure components (e.g., Keycloak token-mapper) can depend on the same shared contracts.

## Requirements

- **Java** 21+
- **Gradle** 8.x (wrapper included)

## Installation

### Option 1: JitPack (recommended)

The library is published to [JitPack](https://jitpack.io/#Bravos-World/Recruitment-Proto-Shared).  
Add the JitPack repository and the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Bravos-World:Recruitment-Proto-Shared:1.0.2")
}
```

For Maven (`pom.xml`):

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Bravos-World</groupId>
    <artifactId>Recruitment-Proto-Shared</artifactId>
    <version>1.0.2</version>
</dependency>
```

> **Note:** The JitPack artifact name (`Recruitment-Proto-Shared`) is derived from the GitHub repository. The Gradle `group` and `artifactId` inside the library are `com.bravos.recruitment:proto-model`.

### Option 2: Maven Local (for local development)

Publish the library to your local Maven repository:

```bash
./gradlew publishToMavenLocal
```

Then add the dependency:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.bravos.recruitment:proto-model:1.0.2")
}
```

### Option 3: Subproject / source dependency

If you already have the repository cloned, you can include it via Gradle composite builds or as a local dependency:

```kotlin
// settings.gradle.kts
includeBuild("../proto-model")
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.bravos.recruitment:proto-model:1.0.2")
}
```

## Usage

After adding the dependency, you can use the generated Protobuf messages and gRPC stubs in your code.

### Example: gRPC client (Java)

```java
import com.bravos.recruitment.proto.user.GetUserPermissionsRequest;
import com.bravos.recruitment.proto.user.GetUserPermissionsResponse;
import com.bravos.recruitment.proto.user.InternalUserInfoServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PermissionClient {

    private final InternalUserInfoServiceGrpc.InternalUserInfoServiceBlockingStub stub;

    public PermissionClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = InternalUserInfoServiceGrpc.newBlockingStub(channel);
    }

    public GetUserPermissionsResponse getPermissions(String username, String userType) {
        GetUserPermissionsRequest request = GetUserPermissionsRequest.newBuilder()
                .setUsername(username)
                .setUserType(userType)
                .build();
        return stub.getUserPermissions(request);
    }
}
```

### Real-world example: Keycloak token-mapper

The [token-mapper](/keycloak/token-mapper) project demonstrates how this library is used inside a Keycloak protocol mapper to enrich access tokens with user permissions:

```java
import com.bravos.recruitment.proto.user.GetUserPermissionsRequest;
import com.bravos.recruitment.proto.user.GetUserPermissionsResponse;
import com.bravos.recruitment.proto.user.InternalUserInfoServiceGrpc;

// See: keycloak/token-mapper/src/main/java/.../PermissionServiceGrpcImpl.java
```

## Protobuf Definitions

### Service: `InternalUserInfoService`

Defined in `src/main/proto/user/InternalUserInfo.proto`.

| RPC | Request | Response | Description |
|-----|---------|----------|-------------|
| `GetInternalUserInfo` | `GetInternalUserInfoRequest` | `GetInternalUserInfoResponse` | Lookup user info by id, username, or email |
| `GetUserPermissions` | `GetUserPermissionsRequest` | `GetUserPermissionsResponse` | Retrieve permission set for a user |

#### Messages

**GetInternalUserInfoRequest**
```protobuf
message GetInternalUserInfoRequest {
  oneof identifier {
    int64 id = 1;
    string username = 2;
    string email = 3;
  }
  string userType = 4;  // CANDIDATE | STAFF
}
```

**GetInternalUserInfoResponse**
```protobuf
message GetInternalUserInfoResponse {
  int64 id = 1;
  string username = 2;
  string email = 3;
  string firstName = 4;
  string lastName = 5;
  bool enabled = 6;
  int64 createdAt = 7;
  bool found = 8;
  string message = 9;
}
```

**GetUserPermissionsRequest**
```protobuf
message GetUserPermissionsRequest {
  string username = 1;
  string userType = 2;  // CANDIDATE | STAFF
}
```

**GetUserPermissionsResponse**
```protobuf
message GetUserPermissionsResponse {
  repeated string permissions = 1;
}
```

## Build from Source

```bash
git clone https://github.com/Bravos-World/Recruitment-Proto-Shared.git
cd Recruitment-Proto-Shared
./gradlew build
```

To re-generate Java classes from `.proto` files:

```bash
./gradlew generateProto
```

## Published Artifacts

| Coordinate | Value |
|------------|-------|
| **Group** | `com.bravos.recruitment` |
| **Artifact** | `proto-model` |
| **Version** | `1.0.2` |
| **JitPack** | `com.github.Bravos-World:Recruitment-Proto-Shared:1.0.2` |

## License

See [LICENSE](./LICENSE).
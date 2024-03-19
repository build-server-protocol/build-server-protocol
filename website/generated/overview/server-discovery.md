---
id: server-discovery
title: Build Server Discovery
---

The Build Server Protocol defines a standard convention for clients to connect
to BSP servers. This protocol has been designed such that:

1. Clients do not require beforehand knowledge about a specific build tool to be
   able to connect to its server.
1. Clients can connect to build tools installed at the machine and at the
   workspace level.
1. Multiple build tools can run in the same workspace directory.
1. Multiple connections to a single build tool can run in the same workspace directory (as long as the build scopes for
   each of the connections do not overlap).

## The BSP Connection Details

The following JSON object defines the BSP connection details:

```ts
export interface BspConnectionDetails {
  /** The name of the build tool. */
  name: String;
  /** The version of the build tool. */
  version: String;
  /** The bsp version of the build tool. */
  bspVersion: String;
  /** A collection of languages supported by this BSP server. */
  languages: String[];
  /** Command arguments runnable via system processes to start a BSP server */
  argv: String[];
}
```

Every build tool supporting BSP must implement a build-tool-specific command to
generate the BSP connection details in one of the standard BSP locations for BSP
connection files.

BSP connection files:

1. must be unique per build tool name, version and build scope to enable different versions
   of the same build tool to select different BSP connection mechanisms.
1. multiple connection files for a single build tool name and version may co-exist in a single workspace root as long as
   there is no overlap in their build scopes; this enables clients to request running separate builds within a single
   workspace.
1. can be updated by the build tool at any point in time, including during the
   startup of the build tool in a workspace.
1. can be added to version control if and only if they do not contain
   machine-dependent information like absolute paths or workspace-specific data.

This is an example of a BSP connection file:

```json
{
  "name": "My Build Tool",
  "version": "21.3",
  "bspVersion": "2.0.0",
  "languages": ["scala", "javascript", "rust"],
  "argv": ["my-build-tool", "bsp"]
}
```

### Default Locations for BSP Connection Files

A BSP connection file can be located in a number of locations. BSP connection
files may be located in the project workspace, or for bsp servers installed
locally, in a system-wide or user-specific data directory, depending on the
operating system:

|           | Unix + Mac                                          | Windows                 |
| --------- | --------------------------------------------------- | ----------------------- |
| Workspace | `<workspace-dir>/.bsp/`                             | `<workspace-dir>\.bsp\` |
| User      | `$XDG_DATA_HOME/bsp/`                               | `%LOCALAPPDATA%\bsp\`   |
|           | `$HOME/Library/Application Support/bsp/` (Mac only) |                         |
| System    | `$XDG_DATA_DIRS/bsp/`                               | `%PROGRAMDATA%\bsp\`    |
|           | `/Library/Application Support/bsp/` (Mac only)      |                         |

Note that:

1. `<workspace-dir>` refers to the workspace base directory.
1. `$XDG_DATA_HOME` and `$XDG_DATA_DIRS` are defined by the
   [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-0.6.html)
1. `%LOCALAPPDATA%` and `%PROGRAMDATA%` are defined by the
   [Windows Documentation](https://docs.microsoft.com/en-gb/windows/desktop/shell/csidl)
   (see also:
   [Default Known Folders](https://docs.microsoft.com/en-gb/windows/desktop/shell/knownfolderid))
1. on Macs, both standard macOS and Unix directories are supported

The workspace location always has higher priority than the user or system
location, so if a client finds a BSP connection file that meets its criteria
inside a workspace location it must pick it over other BSP connection files in
the user or system location.

Workspace-defined build tools must not write BSP connection files to the user or
system locations. That location is only reserved for BSP connection files that
do not contain any workspace-specific data.

### Policy around Connection Files Generation

To have a successful first-time connection to servers, at least one BSP
connection file must exist before users import a project in an IDE or invoke a
BSP client in a workspace.

Build tools installed globally by the user should write a BSP connection file to
the system location to minimize the chances that a client doesn't discover it.
The BSP connection file should also be deleted when the build tool is
uninstalled.

However, in the more general case, build tools are required to implement a
command to generate a BSP connection file either in the user or system location.
This command must be runnable in the workspace base directory.

With such command, the following workflows become possible:

1. Users can manually install a BSP connection file for any build tool.
1. Clients can implement smart discovery capabilities to:
   1. Detect the build tool(s) used in a workspace.
   1. Invoke the command to generate a BSP connection file for them.

These workflows help improve the user experience for clients that want a more
out-of-the-box experience and provide a escape hatch for users to generate BSP
connection files for exotic and unsupported build tools.

### Build Tool Commands to Start BSP Servers

The most important data field in the connection file is the `argv` JSON field.
The `argv` field contains the command arguments that start a BSP server via
system process.

Clients must meet the following requirements when using `argv` via system
process:

1. The first element of the `argv` collection can be a simple name, a relative
   path or an absolute path. A relative path is always relative to the workspace
   base directory, so the client must prepend the value of the workspace folder
   to the relative path before spawning `argv`.
1. `argv` must always be invoked in the workspace base directory.
1. `argv` must be invoked with the same environment variables of the client.

Build tools must make sure that their `argv` invocation:

1. Creates a fresh BSP connection to a server every time. This is required in
   case there is more than one client connecting to a server or a server crashes
   and a client wants to reconnect.
1. Uses `stdin` to send messages and `stdout` to receive responses to/from the
   BSP server.
1. Uses `stderr` to report execution progress to the user.

The use of `stdin` and `stdout` to communicate with the build server simplifies
the life of clients and allows build tools to implement their own underlying
protocol to connect to a local/remote build tool instance/daemon.

In addition, build tools can use the `argv` invocation for other purposes such
as:

1. Spawn a daemon if it's not already running.
1. Install the build tool if it's not already installed in a user's machine.

#### Example with `my-build-tool`

To illustrate the responsibilities of the build tool, let's go through a small
example where:

1. The `my-build-tool` build tool is installed in the user's machine.
1. The `argv` field is set to `["my-build-tool", "bsp"]`.
1. There is no running build tool instance in a workspace directory
   `<workspace>`.
1. `my-build-tool` supports BSP connections with a running instance of the build
   tool via
   [UNIX domain sockets](https://en.wikipedia.org/wiki/Unix_domain_socket) and
   [Windows Named Pipes](https://docs.microsoft.com/en-us/windows/desktop/ipc/named-pipes).

The invocation of `my-build-tool bsp`, with current working directory
`<workspace>`, will need to:

1. Run a background process of the build tool for the given `<workspace>`.
1. Pick the best way to connect to the running process depending on the machine
   it runs. For example, it would use UNIX sockets in a Linux machine.
1. Fire up a BSP server in the build tool with script-specific connection
   details. In the case of Unix sockets, the script will generate the socket
   file and pass it to the background process of the build tool.
1. Connect to the running BSP server, forward anything that comes from `stdin`
   to the BSP server and print anything that comes from the server's output
   streams to `stdout`. Execution progress will be shown in `stderr`.

If the build tool is already running for a given project, the `argv` invocation
will only perform the last two steps.

## Clients Connecting to BSP Servers

The BSP Server Discovery aims to simplify clients the process of connecting to
servers.

Clients can connect to servers by locating connection files in the standard BSP
locations. BSP clients must look up connection files first in the bsp user
location and, only if the lookup of a connection file meeting certain criteria
fails, continue the search in the system location.

When more than a single BSP connection file is found, BSP clients can use connection
metadata to pick only the BSP servers they are interested in. If there are still
ambiguities, BSP clients are free to choose how to react, for example by asking
the end user to select a build server. Clients may also make it possible for the user to select
multiple connections, effectively having multiple, separate BSP builds running at one time.

When no BSP connection file is found (because, for example, the user has not run
the build tool command to generate BSP connection details), the BSP client can:

1. Fail gracefully.
1. Ask users to type the command to generate the BSP connection details with
   their preferred build tool and then connect to the BSP server.
1. Discover the build tool used in a project manually, run the command to
   generate the BSP connection details and then connect to the BSP server.

When BSP clients have found a valid connection file, they can connect to the
server by running the `argv` invocation via system process; listening to its
system output and writing to its system input. If the `argv` invocation fails,
the output in `stderr` must be shown to the user.

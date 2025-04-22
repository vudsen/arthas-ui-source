# arthas-ui
<!-- Plugin description -->

An IntelliJ IDEA Plugin that manage [Arthas](https://github.com/alibaba/arthas) connections for you.


## Features

- Download the toolchain automatically ([Arthas](https://github.com/alibaba/arthas), [Jattach](https://github.com/jattach/jattach)).
- Support OGNL expression to filter the JVM.
- Arthas Grammar check.
- Command Execution History.

Check our [documentation](https://arthas-ui.pages.dev/) for more information.

<!-- Plugin description end -->

## Quick Start

This tutorial will teach you how to attach to your local jvm.

### Configure a host machine
Open `Settings` -> `Tools` -> `Arthas UI`, and add a host machine.

> [!WARNING] 
> We currently do not have the form validation. Please ensure that every input is filled out.

About `Connect Config`:

- LOCAL: use your local machine where the ide runs.
- SSH: use remote machine via ssh.

We’ll use LOCAL here.

About `Jvm Provider Config`:

- Local: use the locally running JVM.
- Docker: use the jvm running in docker

Choose Local here, and configure your JDK home and Arthas home.

Finally, click the `OK` and save the configuration.

### Select a jvm

Open `ArthasUI` in the top right corner of your toolbar. It will display as a tree view.
Expand the root node, and double-click the `Local JVM`:

![toolwindow](/image/toolwindow.png)

Then, double-click the JVM you want to attach to. A query console will open — type any Arthas command here and run it
by clicking the green run button.
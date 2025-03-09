# arthas-ui

An IntelliJ IDEA Plugin that manage [Arthas](https://arthas.aliyun.com/) connections for you.

> [!CAUTION]
> It's currently in the **alpha version**, which may contain critical bugs, especially under poor network conditions,
> that could affect your IDEA(E.g. freeze your IDE). Please use it with caution!
>
> And we still have a lot of features that have not been done; please stay tuned.

## Features

- [x] Attach to the local JVM
- [x] Attach to the JVM in docker 
- [ ] Attach to the JVM in kubernetes (coming soon)
- [x] Basic command completion
- [ ] Arthas command grammar check (unstable)

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
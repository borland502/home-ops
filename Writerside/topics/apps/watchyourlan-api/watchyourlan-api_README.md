# watchyourlan-api

## Overview

This application is intended to be a companion to the [WatchYourLan](https://github.com/aceberg/WatchYourLAN) project.
As the name suggests this project creates an express API to query the inventory established in the `now` table of
the sqlite database created by WatchYourLan.

## Quickstart

```shell
nx run watchyourlan-api:build
nx run watchyourlan-api:serve
```

## Running e2e tests

After the server is up.  Execute the following command to run the e2e tests.

```shell
nx run watchyourlan-api:e2e
```

# ClockSync
##Henrik Akesson & Fabien Salathe

#### Usage of Slave

```
java Slave [<host_name>]
```

Where **host_name** (optional) is the address of the host name.
Default value: localhost
Example:
```
java Slave 192.168.99.100
```

### Usage of Master
```
java Master [<number_of_slaves>]
```
Where **number_of_slaves** (optional) is the number of slaves to broadcast to and listen for.
Default value: 3
Example:
```
java Master 2
```
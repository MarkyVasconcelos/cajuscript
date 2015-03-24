# Comments #

All signs of comments allowed:
```
     -- Comment...

     // Comment...

     \ Comment...
```

Only is possible comment a entire line:
```
    x = 1 ?
        // Comments...
        x += 1
    ?
```

This comment is **not** allowed:
```
    x = 1 ? // Comments...
        x += 1
    ?
```
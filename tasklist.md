# Task list #

All tasks in development:

  * Array support. (_eduveks_):
```
      array = {'a', 1, true}
      map = {'key1' = 'a', 'key2' = 1, 'key3' = true}
```
  * Safe-null-operators (_mark.vscs_, _eduveks_)
```
      s = person?.getAddress()?:defaultAddress.getPostalCode()?.getValue();
```
  * Class implementation. (_?_)
```
      Class : InterfaceClass, ExtendsClass ##
        param = ''

        \ Useful methods:

        classInit #
          param = 'x'
        #
        methodMissing (method, args) #
        #
        paramMissing (param, value) #
        #
      ##
```
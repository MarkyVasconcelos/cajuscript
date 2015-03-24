# Sintaxe em Português #

Exemplo de uma sintaxe em Português usando o CajuScript.

Na mesma pasta em que esta o jar do CajuScript crie o arquivo "carregaSintaxePortugues.cj" que ira carregar a sintaxe, com o seguinte conteúdo:

```
    $java.util.regex

    syntaxPT = org.cajuscript.Syntax()
    syntaxPT.setIf(Pattern.compile("^[\\s+s|s]e\\s*([\\s+|[\\s*\\(]].+)\\s*faz"))
    syntaxPT.setElseIf(Pattern.compile("^[\\s+o|o]u\s+se\\s*([\\s+|[\\s*\\(]].+)\\s*faz"))
    syntaxPT.setElse(Pattern.compile("^[\\s+o|o]u\\s+senao\\s+fa[z\\s+|z]$"))
    syntaxPT.setIfEnd(Pattern.compile("^[\\s+f|f]i[m\\s+|m]$"))
    syntaxPT.setLoop(Pattern.compile("^[\\s+e|e]nquanto\\s*([\\s+|[\\s*\\(]].+)\\s*faz"))
    syntaxPT.setLoopEnd(Pattern.compile("^[\\s+f|f]i[m\\s+|m]$"))
    syntaxPT.setTry(Pattern.compile("^[\\s+t|t]enta\\s*([\\s+|[\\s*\\(]].+)\\s*faz"))
    syntaxPT.setTryCatch(Pattern.compile("^[\\s+p|p]ega\\s+o\\s+err[o\\s+|o]$"))
    syntaxPT.setTryFinally(Pattern.compile("^[\\s+f|f]inalment[e\\s+|e]$"))
    syntaxPT.setTryEnd(Pattern.compile("^[\\s+f|f]i[m\\s+|m]$"))
    syntaxPT.setFunction(Pattern.compile("^[\\s+f|f]uncao\\s*([\\s+|[\\s*\\(]].+)\\s*faz"))
    syntaxPT.setFunctionEnd(Pattern.compile("^[\\s+f|f]i[m\\s+|m]$"))
    syntaxPT.setNull(Pattern.compile("nulo"))
    syntaxPT.setReturn(Pattern.compile("retorna"))
    syntaxPT.setImport(Pattern.compile("importa\\s+"))
    syntaxPT.setRootContext(Pattern.compile("raiz\\."))
    syntaxPT.setContinue(Pattern.compile("continua"))
    syntaxPT.setBreak(Pattern.compile("para"))
    syntaxPT.setOperatorOr(Pattern.compile("[\\s+|)\\s*]ou[\\s+|(\\s*]"))
    syntaxPT.setOperatorAnd(Pattern.compile("[\\s+|)\\s*]e[\\s+|(\\s*]"))

    org.cajuscript.CajuScript.addGlobalSyntax("PT", syntaxPT)

    $"exemploSintaxePortugues.cj"
```

E também o arquivo "exemploSintaxePortugues.cj" que é o arquivo com a nova sintaxe me português, com o seguinte conteúdo:

```
    caju.syntax: PT

    importa java.lang

    // IF = SE
    x = 10
    se x < 10 e x > 0 faz
        System.out.println("X menor que 10!")
    ou se x > 10 e x ! 10 faz
        System.out.println("X maior que 10!")
    ou se x = 10 ou x ! 0 faz
        System.out.println("X igual 10!")
    ou senao faz
        System.out.println("X menor que 1!")
    fim

    // LOOP = ENQUANTO
    x = 0
    enquanto x < 100 e x >= 0 faz
        System.out.println(x)
        x += 1
        se x = 10 faz
            para
        ou senao faz
            continua
        fim
    fim

    // FUNCTION = FUNCAO
    x = 5
    funcao addWithX(v1, v2) faz
        retorna raiz.x + v1 + v2
    fim
    x = addWithX(10, 20)
    System.out.println("X = "+ x)

    // TRY/CATCH = TENTA/PEGA
    tenta e faz
        "".substring(0, -1)
    pega o erro
        System.out.println("Erro: "+ e.getMessage())
    finalmente
        System.out.println("Finalmente...")
    fim

    System.out.println("\n\nSyntax em portugues executada com sucesso!\n")

    Thread.sleep(10000)
```

Agora é só executar com o comando:

**java -jar cajuscript.jar carregaSintaxePortugues.cj**


Pronto! :P

Não convêm usar acentos na sintaxe, por que pode haver alguns problemas de encoding ao transportar o arquivo por vários sistemas operacionais, como do linux para o windows. Mas quem não arrisca não petisca.

Também em:

http://eduveks.blogspot.com/2008/03/que-tal-uma-sintaxe-em-portugues.html
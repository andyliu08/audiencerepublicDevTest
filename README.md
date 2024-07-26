# How to execute the code using Jshell

Suppose you already install Jshell in your machine

1. Open the code in Jshell 

```
jshell> /open Solution.java
```

2. Running the code

```
jshell> Solution.main("graph -N 8 -S 15")
```

After that you can see the results print out on the console.



Results including:

1. Print out the randomly generated connected direction graph
2. The eccentricity of all the Vertex
3. The diameter and radius of the randomly generated graph
4. The shortest distance path between 2 random using Dijkstra algorithm

# Note

1. As Jshell can only open one file to be executed, I have to move all the classes into the file "Solution.java", but Ideally, I would like use the following file structure
   Algorithm.java
   GraphHashMap.java
   GraphInterface.java
   RandomUtil.java
   Solution.java
   

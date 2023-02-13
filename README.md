# Cracking_the_Enigma_Machine

In this project you will find an implementation of a competition between different automatic decoding teams.
On the one hand, we can load different settings and configurations of the machines and distribute encrypted messages.
On the other hand, there are a number of automatic decoding teams that differ among themselves in the various parameters (number of agents, task size, etc.) 
and compete among themselves to see who will succeed in deciphering the encrypted message first (and if at all...).

In this project you will find an implementation of a competition between different automatic decoding teams.
On the one hand, we can load different settings and configurations of the machines and distribute encrypted messages.
On the other hand, there are a number of automatic decoding teams that differ among themselves in the various parameters (number of agents, task size, etc.) and compete among themselves to see who will succeed in deciphering the encrypted message first (and if at all...)

## There are 4 central entities:

• UBoat – a German submarine responsible for determining an initial code for the machine, 
  distributing an encrypted message and deciding which of the different teams was able to decode the message accurately (winner of the competition).
  
• Allies - decoding teams of the allies competing among themselves to be the first to succeed in deciphering the encrypted message transmitted by the UBoat.

• Agent – agents who are members of the various decoding teams and are responsible for performing the decoding tasks themselves. 
  Each agent is a member of exactly one team.
  
• Battlefield – the battlefield where the competition takes place. 
  Battleground definitions include the name, how many decryption teams are participating in each competition, 
  and the difficulty level of the decryption.
  Exactly one German submarine participates in each battle. and one or more decoding teams.

There is a server (tomcat) that will contain the definitions of the various competitions held in the system.
Each type of user who connects to the server begins his journey on the system registration page, 
where he has to choose whether he is a uboat, allies or agent type entity.

A Uboat entity can upload an XML file that defines the competition and its various details.
Only Uboat can set up a contest by uploading a file.
Uboat can only create its own new competition. He cannot participate in a competition that already has another Uboat in it. 
(In other words, there is exactly one Uboat in each competition).

An Allies type entity can connect to the server and choose the competition it wants to register for 
(only one competition at any given time). Each Allies type player is actually an automated server-side generator. 
Since there are several Allies in the competition - there are also several different decoders on the server side. in parallel.

An Agent type entity can connect to the server and select the Allies team it is a member of. 
Each Agent is a member of exactly one team. This detail is determined during registration and cannot change throughout the life of the application (of the Agent)


## Course of the competition:
The competition starts when all players (Allies, Uboat) announce that they are ready. 
The UBoat will choose a code and encrypt a certain message (a valid message, from words in the dictionary). 
The encrypted message is transmitted and distributed to all the Allies teams participating in the competition. 
Each team assigns the various tasks to the agents it works with. 
The agents in turn start the decoding process and send possible candidates back to DM (Decrypt Manager) and he in turn sends them back to UBoat.
The UBoat receives a collection of candidates from the various Allies teams and only he will be the one to determine who the winning candidate is (since only he knows which string matches the source string).

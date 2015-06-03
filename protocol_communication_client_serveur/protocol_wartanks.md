# Wartanks Protocol Secifications


## Handshake

Handshake must be the first message send to the server, the server must reply the same
message.

|    Name     |   Size   |               Description                |
|-------------|----------|------------------------------------------|
| strlen      | byte     | Size of client_name                      |
| client_name | byte[]   | String name of the client                |
| client_id   | byte[20] | Random identifier generate by the client |


## Message basic format

The communication between the client and the server is made with differrent messages.
Here the basic format of these messages:

|  Name   |       Size       |        Description         |
|---------|------------------|----------------------------|
| length  | int32            | Size of the message        |
| ID      | byte             | Identifiant of the message |
| payload | byte[length - 1] | Content of the message     |



## Message IDs
| ID |       Description       |
|----|-------------------------|
|  0 | keep-alive              |
|  1 | Changement of direction |
|  2 | Shoot                   |
|  3 | Give bonus              |
|  4 | Map state               |
|  5 | Player state            |
|  6 | Bullet state            |
|  7 | Score                   |
|  8 | Game over               |



## Messages details
###Keep-alive
Format \<length=1>\<ID=0>\<no payload>
This message contain no payload.


### Direction
Format \<length=2>\<ID=1>\<direction ID (byte)>

The client send this message when he change of direction

### Shoot
Format \<length=1>\<ID=2>\<no payload>

This message is send by the client when he shoot.

### Bonus
Format \<length=2>\<ID=3>\<bonus ID (byte)>

This message is send by the server when a client reach a bonus.

### Map
Format \<length=1+X>\<ID=4>\<map (byte[X])>

This message is send by the server each Y seconds to update client map state.
(for example: bonus emplacement)

### Player
Format \<length=1+X>\<ID=5>\<map (byte[X])>
This message is send by the server to update ennemies state on client map.

### Bullet
Format \<length=6>\<ID=6>\<pos_x (int16)>\<pos_y (int16)>\<direction ID (byte)>

This message is send by the server to update ennemie's bullets on client map.

### Score
Format \<length=1+X>\<ID=7>\<score_data (byte[X])>

This message is send by the server to update client scoreboard.

### Game Over
Format \<length=1+X>\<ID=8>\<is_winner (byte)>

This message is send by the server at the end of the game.

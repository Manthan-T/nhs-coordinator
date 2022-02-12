# NHS Coordinator

An app designed for the CodeSec competition. It's objective is to allow staff in a hospital to attend to patients the moment they need help.

Different modules of the project are stored in different branches within this repo:
- master: Main App loaded onto phones that notifies hospital staff as above
- imagerecognition: Two programs (in addition to a MySQL database) loaded onto one computer per floor that monitor all rooms
- server: The controlling centralised server that communicates with the above two to manage the whole system

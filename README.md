# NHS Coordinator

An app designed for the CodeSec competition. It's objective is to allow staff in a hospital to attend to patients the moment they need help.

Different modules of the project are stored in different branches within this repo:
- master: Main App loaded onto phones that notifies hospital staff as above (the main code is located within the "core" and "android" modules (then go to `src` and then through     all of the foldersuntil you get to the `main` folder
- imagerecognition: Two programs (in addition to a MySQL database) loaded onto one computer per floor that monitor all rooms
- server: The controlling centralised server that communicates with the above two to manage the whole system (includes main code files, but the file limit prevented easily         uploading all of the code

For making a floorplan, please use:
- 0x79D683 for patient rooms
- White for patient room numbers
- 0xD15CC7 for the floor number
- 0xD3000E for non-patient rooms
- 0x00A0E0 for pathways
- 0x0059E0 for the surroundings

Please also provide your computer's IP address and the port number for the custom files.

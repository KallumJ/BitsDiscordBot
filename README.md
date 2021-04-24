# Bits Discord Bot

A Discord Bot written in Java using the [Java Discord Wrapper (JDA)](https://github.com/DV8FromTheWorld/JDA) to
implement desired features for the Bits Minecraft/Gaming Discord Server.

Main features include:

* A search command to perform lookups on the [Bits+ wiki](https://wiki.plus.bits.team) and return the closest result.
* Voice Recognition (using the [Vosk](https://alphacephei.com/vosk/) Open Source Library) and Speech Synthesis (
  using [Pico Text-to-Speech](https://www.openhab.org/addons/voice/picotts/)) in voice channels.
* Chat Bot functionality using the [Program AB AIML implementation](https://code.google.com/archive/p/program-ab/) to
  analyse queries and provide custom responses modelled in AIML.
* Manage the assigning and removal of server roles to allow users to be contacted based on the commands.games they like
  to play.
* A community events system that allows admins to schedule events, and allows users to rsvp to them, with all
  information stored in a MySQL database.
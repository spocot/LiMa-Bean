LiMa Bean is a Li(brary) Ma(nager) written in java (hence the bean). You create a new LibHandler class by passing the constructor the url of your file folder on your web server and the path of the folder that you want it to download everything to. It will look for a file called filelist.txt in the url file folder and read it's contents, each line providing the name of a different library. It then downloads these libraries from the web server and extracts them to the folder specified. It is an implementation of the runnable class so that you can run it in an adjacent thread. It currently has (what I think) are in depth messages printed to the console. I hope to modify this and apply it to a GUI to make an updater / installer for a game or another piece of software.
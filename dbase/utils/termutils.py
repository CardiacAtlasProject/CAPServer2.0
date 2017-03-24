class bcolors:
    DEBUG = '\033[95m'     # magenta
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'    # yellow
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def debug(text):
    print bcolors.DEBUG + '[debug] ' + bcolors.ENDC + text

def error(text):
    print bcolors.FAIL + '[error] ' + text + bcolors.ENDC

def ok(text):
    print bcolors.OKGREEN + '[success] ' + bcolors.ENDC + text

def warn(text):
    print bcolors.WARNING + '[warn] ' + bcolors.ENDC + text

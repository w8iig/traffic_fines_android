import codecs

def helper_int(str):
    try:
        return int(str)
    except ValueError as e:
        print 'helper_int: Invalid int string "%s"' % (str)
        print e
        return 0

def helper_file_write(path, contents):
    try:
        f = codecs.open(path, 'w', encoding='utf-8')
        f.write(contents)
        f.close
    except IOError as e:
        print 'helper_file_write: Failed writing to %s' % (path)
        print e

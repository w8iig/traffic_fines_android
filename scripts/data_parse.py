import pytoml
import re

def parse(toml_path, java_root_package, java_class, res_name):
    toml_data = False
    re_pattern_fine = re.compile('^(\d+)\-(\d+)$')
    
    with open(toml_path) as toml_file:
        toml_data = pytoml.loads(toml_file.read())
    
    if toml_data == False:
        print 'Unable to loads TOML'
        return
    
    title = toml_data['title']
    fines = toml_data['fines']
    
    # declare java package, imports and class
    output_java = 'package %s.data;\n\nimport %s.R;\n\npublic class %s extends DataAbstract {\n' % (java_root_package, java_root_package, java_class)
    # implement DataAbstract::getTitleResId
    output_java += '\n\t@Override\n\tpublic int getTitleResId() {\n\t\treturn R.string.%s_title;\n\t}\n' % (res_name)

    # start implementing DataAbstract::getNameResIds()
    output_java_getNameResIds = '\n\t@Override\n\tpublic int[] getNameResIds() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getDescriptionResIds()
    output_java_getDescriptionResIds = '\n\t@Override\n\tpublic int[] getDescriptionResIds() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getValuePairs()
    output_java_getValuePairs = '\n\t@Override\n\tpublic int[] getValuePairs() {\n\t\treturn new int[] {\n'
    
    # prepare resource XML header
    output_res = '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n'
    output_res +='\t<string name="%s_title">%s</string>\n' % (res_name, title);
    
    is_first = True
    for fine_id in fines:
        # parsing the fine values
        value_low = 0
        value_high = 0
        if fines[fine_id].has_key('fine'):
            fine_value = fines[fine_id]['fine']
            re_match = re_pattern_fine.search(fine_value)
            if re_match != None:
                value_low = helper_int(re_match.group(1))
                value_high = helper_int(re_match.group(2))
            else:
                value_high = helper_int(fine_value)
        
        # implementing DataAbstract::getNameResIds()
        output_java_getNameResIds += '%s\t\t\tR.string.%s_%s_name' % (('' if is_first else ',\n'), res_name, fine_id)
        # implementing DataAbstract::getDescriptionResIds()
        output_java_getDescriptionResIds += '%s\t\t\tR.string.%s_%s_description' % (('' if is_first else ',\n'), res_name, fine_id)
        # implementing DataAbstract::getValuePairs()
        output_java_getValuePairs += '%s\t\t\t// %s\n\t\t\t%d, %d' % (('' if is_first else ',\n'), fine_id, value_low, value_high)
        
        # prepare string for name
        output_res +='\t<string name="%s_%s_name">%s</string>\n' % (res_name, fine_id, fines[fine_id]['name']);
        # prepare string for description
        output_res +='\t<string name="%s_%s_description">%s</string>\n' % (res_name, fine_id, fines[fine_id]['description']);
        
        is_first = False

    # finish implementing DataAbstract::getNameResIds()
    output_java += '%s\n\t\t};\n\t}\n' % (output_java_getNameResIds)
    # finish implementing DataAbstract::getDescriptionResIds()
    output_java += '%s\n\t\t};\n\t}\n' % (output_java_getDescriptionResIds)
    # finish implementing DataAbstract::getValuePairs()
    output_java += '%s\n\t\t};\n\t}\n' % (output_java_getValuePairs)
    
    # final closing brace for java class
    output_java += '}'
    
    # final closing tag for resource XML
    output_res += '</resources>'
    
    helper_file_write('src/%s/data/%s.java' % (java_root_package.replace('.', '/'), java_class), output_java)
    helper_file_write('res/values/%s.xml' % (res_name), output_res)

def helper_int(str):
    try:
        return int(str)
    except ValueError as e:
        print 'helper_int: Invalid int string "%s"' % (str)
        print e
        return 0

def helper_file_write(path, contents):
    try:
        f = open(path, 'w')
        f.write(contents)
        f.close
    except IOError as e:
        print 'helper_file_write: Failed writing to %s' % (path)
        print e

if (__name__ == '__main__'):
    parse('data/bike.toml', 'com.w8iig.trafficfines', 'DataBike', 'data_bike')
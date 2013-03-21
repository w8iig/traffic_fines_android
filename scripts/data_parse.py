import pytoml
import re

import helper

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
    output_java = u'package %s.data;\n\nimport %s.R;\n\npublic class %s extends DataAbstract {\n' % (java_root_package, java_root_package, java_class)
    # implement DataAbstract::getTitleResId
    output_java += u'\n\t@Override\n\tpublic int getTitleResId() {\n\t\treturn R.string.%s_title;\n\t}\n' % (res_name)

    # start implementing DataAbstract::getUniqueIds()
    output_java_getUniqueIds = u'\n\t@Override\n\tpublic String[] getUniqueIds() {\n\t\treturn new String[] {\n'
    # start implementing DataAbstract::getNameResIds()
    output_java_getNameResIds = u'\n\t@Override\n\tpublic int[] getNameResIds() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getDescriptionResIds()
    output_java_getDescriptionResIds = u'\n\t@Override\n\tpublic int[] getDescriptionResIds() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getValuePairs()
    output_java_getValuePairs = u'\n\t@Override\n\tpublic int[] getValuePairs() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getLicenseDays()
    output_java_getLicenseDays = u'\n\t@Override\n\tpublic int[] getLicenseDays() {\n\t\treturn new int[] {\n'
    # start implementing DataAbstract::getVehicleDays()
    output_java_getVehicleDays = u'\n\t@Override\n\tpublic int[] getVehicleDays() {\n\t\treturn new int[] {\n'
    
    # prepare resource XML header
    output_res = u'<?xml version="1.0" encoding="utf-8"?>\n<resources>\n'
    output_res += u'\t<string name="%s_title">%s</string>\n' % (res_name, title);
    
    is_first = True
    fine_ids = sorted(fines.keys())
    for fine_id in fine_ids:
        if (fine_id == 'test'):
            continue
        
        # parsing the fine values
        value_low = 0
        value_high = 0
        if fines[fine_id].has_key('fine'):
            fine_value = fines[fine_id]['fine']
            re_match = re_pattern_fine.search(fine_value)
            if re_match != None:
                value_low = helper.helper_int(re_match.group(1))
                value_high = helper.helper_int(re_match.group(2))
            else:
                value_high = helper.helper_int(fine_value)

        # parsing license days
        license_days = 0
        if fines[fine_id].has_key('license_days'):
            license_days = helper.helper_int(fines[fine_id]['license_days'])
        # parsing vehicle days
        vehicle_days = 0
        if fines[fine_id].has_key('vehicle_days'):
            vehicle_days = helper.helper_int(fines[fine_id]['vehicle_days'])

        # implementing DataAbstract::getUniqueIds()
        output_java_getUniqueIds += u'%s\t\t\t"%s"' % (('' if is_first else ',\n'), fine_id)        
        # implementing DataAbstract::getNameResIds()
        output_java_getNameResIds += u'%s\t\t\tR.string.%s_%s_name' % (('' if is_first else ',\n'), res_name, fine_id)
        # implementing DataAbstract::getDescriptionResIds()
        output_java_getDescriptionResIds += u'%s\t\t\tR.string.%s_%s_description' % (('' if is_first else ',\n'), res_name, fine_id)
        # implementing DataAbstract::getValuePairs()
        output_java_getValuePairs += u'%s\t\t\t// %s\n\t\t\t%d, %d' % (('' if is_first else ',\n'), fine_id, value_low, value_high)
        # implementing DataAbstract::getLicenseDays()
        output_java_getLicenseDays += u'%s\t\t\t// %s\n\t\t\t%d' % (('' if is_first else ',\n'), fine_id, license_days)
        # implementing DataAbstract::getVehicleDays()
        output_java_getVehicleDays += u'%s\t\t\t// %s\n\t\t\t%d' % (('' if is_first else ',\n'), fine_id, vehicle_days)
        
        # prepare string for name
        output_res += u'\t<string name="%s_%s_name">%s</string>\n' % (res_name, fine_id, unicode(fines[fine_id]['name'], 'utf-8'));
        # prepare string for description
        output_res += u'\t<string name="%s_%s_description">%s</string>\n' % (res_name, fine_id, unicode(fines[fine_id]['description'], 'utf-8'));
        
        is_first = False

    # finish implementing DataAbstract::getUniqueIds()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getUniqueIds)
    # finish implementing DataAbstract::getNameResIds()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getNameResIds)
    # finish implementing DataAbstract::getDescriptionResIds()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getDescriptionResIds)
    # finish implementing DataAbstract::getValuePairs()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getValuePairs)
    # finish implementing DataAbstract::getLicenseDays()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getLicenseDays)
    # finish implementing DataAbstract::getVehicleDays()
    output_java += u'%s\n\t\t};\n\t}\n' % (output_java_getVehicleDays)
    
    # final closing brace for java class
    output_java += u'}'
    
    # final closing tag for resource XML
    output_res += u'</resources>'
    
    helper.helper_file_write('src/%s/data/%s.java' % (java_root_package.replace('.', '/'), java_class), output_java)
    helper.helper_file_write('res/values/%s.xml' % (res_name), output_res)

if (__name__ == '__main__'):
    parse('data/bike.toml', 'com.w8iig.trafficfines', 'DataBike', 'data_bike')

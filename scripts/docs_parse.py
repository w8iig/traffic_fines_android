import codecs
import markdown

import helper

def parse(markdown_path, res_name):
    markdown_html = False
    
    with codecs.open(markdown_path, encoding='utf-8') as markdown_file:
        markdown_html = markdown.markdown(markdown_file.read())
    
    html = u'<!doctype html>\n'
    html += u'<html>\n\t<head>\n'
    html += u'\t\t<meta charset="utf-8">\n'
    html += u'\t\t<title>%s</title>\n' % (markdown_path)
    html += u'\t</head>\n\t<body>\n'
    html += markdown_html
    html += u'\n\t</body>\n</html>'
    
    helper.helper_file_write('res/raw/%s.html' % (res_name), html)

if (__name__ == '__main__'):
    parse('data/docs/20100402_34_2010_ndcp.markdown', 'docs_34_2010_ndcp')
    parse('data/docs/20121110_71_2012_ndcp.markdown', 'docs_71_2012_ndcp')

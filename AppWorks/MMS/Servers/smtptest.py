from smtplib import SMTP

server = 'localhost'#'usw-sf-fw2.sourceforge.net'
port = 2525
fromx = 'foo'
to = ['bar']
msg = ['hello', 'world']

if 1:
    server = SMTP (server, port)
    server.set_debuglevel(1)
    server.sendmail(fromx, to, msg)
    server.quit()


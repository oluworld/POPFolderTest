from etoffiutils import quickWrite, b64_encode, qpi_encode, \
	x2_encode, ensure_directory_present, true, false
from AppWorks.Jobs import Job
#from AppWorks.MMS.MailExtract import MailExtract
from AppWorks.MMS import MailExtract as MailExtract_
import time, string

MailExtract = MailExtract_.MailExtract

#
# OnetAddNewsgroupMessage.py
# --------------------------
#
# j.spec_list ->
# 	server, group_name, msgnum, options
# j.spec_list['options'] ->
#	store-in-filesystem
#	mangle-server-name
#	mangle-group-name
#	save-headers
#	extract-contents
#	delete-orig
# j.params ->
#	(msg_lines, oixfs_attr_dict)
#		|			+-> ignored (for now...)
#		+---> obvious
#

mangle_name = b64_encode

msg_st_root = ''
onet_base   = ''
	
def cgi_escape (s, quote=None):
    """Replace special characters '&', '<' and '>' by SGML entities."""
    s = string.replace(s, "&", "&amp;") # Must be done first!
    s = string.replace(s, "<", "&lt;")
    s = string.replace(s, ">", "&gt;",)
    if quote:
        s = string.replace(s, '"', "&quot;")
    return s

def print_spec_list(sl):
	for each in sl.keys():
		print each + '\t\t' + sl[each]

def extract_headers_from_message (msg):
	if type(msg) == type(''):
		print 'oops'
	r = []
	for each in msg:
#		print each
		if each == '':
			break
		r.append (each)
	return r

def read_cfg_file ():
	try:
		f = open ('OnetAddNewsgroupMessage.rc')
		f.readline()[:-1]
		l1 = f.readline()[:-1]
		f.readline()[:-1]
		l2 = f.readline()[:-1]
		f.close()
	except :
		l1, l2 = 'c:/_', 'e:/_onet/by-%s'
	try:
		f = open ('OnetAddNewsgroupMessage.rc', 'w')
		f.write ('# message_store_root: ~/local/data/GetNewsX/\n')
		f.write ('%s\n' % l1)
		f.write ('# oanm_base: ~onet-storage/by-\%s\n')
		f.write ('%s\n' % l2)
		f.close()
	except:
		print 'error writing config'
	global msg_st_root, onet_base
	msg_st_root = l1
	onet_base   = l2
##	print 'msg_st_root', msg_st_root
##	print 'onet_base', onet_base

def get_news_msg_st_root ():
	return msg_st_root

def get_onet_base (x):
	return onet_base % x
def res_id_to_path (x):
	return x.replace ('-','/')

def today_date():
	r = time.strftime('%Y-%b-%d (%H%M)', time.localtime(time.time()))
	return r

def extract_info_from_msg (headers):
	h = {}
	for each in headers:
		try:
			p = each.index (':')
			h[each[:p].lower()]=each[p+1:]
#			print each[:p], each[p+1:]
		except ValueError:
			pass

	aa = h['from']
	author, author_email = '', ''
	aa = aa.split()
	if len(aa) == 2:
		author, author_email = aa[0], aa[1]
	else:
		author, author_email = aa[0][:aa[0].find('@')], aa[0]
	msgid, date = (h['message-id'], h['date'])
	try:
		posting_host = h['nntp-posting-host']
	except KeyError:
		posting_host = ''
	
	return  author, author_email, msgid, date, posting_host 

def do_job(j, jm):
	print 'OnetAddNewsgroupMessage ======================================================='

#	print j.job_type
	print_spec_list (j.spec_list)

	read_cfg_file ()

	msg_lines = j.params[0]	
	oix_attrs = j.params[1]	

	if msg_lines == None: return
		
	server = j.spec_list['server']
	msgnum = j.spec_list['msgnum']
	grpnam = j.spec_list['group_name']

	xserver, xgrpnam = server, grpnam

#	s = j.spec_list['options']
	l = j.spec_list['options'].split()

##	print oix_attrs

	delete_orig = 0
	extract_contents = 0
	
	if len(l):

#		store-in-filesystem
#		mangle-server-name
#		mangle-group-name
#		save-headers

		if "mangle-server-name" in l:
			xserver = mangle_name (server)
		if "mangle-group-name" in l:
			xgrpnam = mangle_name (grpnam)
		if "delete-orig" in l:
			delete_orig = 1
		if "extract-contents" in l:
			extract_contents = 1
		if "store-in-filesystem" in l:
	
			dl = '%s/%s/%s/' % (get_news_msg_st_root(), xserver, xgrpnam)
			ensure_directory_present (dl)
			sl = dl + msgnum
			quickWrite (sl, msg_lines, true)
			
			if "save-headers" in l:
				quickWrite (sl+".headers", extract_headers_from_message (msg_lines), true)

	base = get_onet_base ('basic-resource-id')
	resid = 'OnetAddNewsgroupMessage1-%s-%s-%s' % (xserver, xgrpnam, msgnum)
	fn_ = '%s/%s' % (base, res_id_to_path (resid))
	ensure_directory_present (fn_)
	fn = '%s/%s' % (fn_, msgnum)

	# -------------------
	#  start writing xml
	# -------------------

	if not delete_orig:
		if msg_lines is not None:
			quickWrite (fn, msg_lines, true)

	dpyname = '%s in %s at %s' % (msgnum, grpnam, server)
	headers = extract_headers_from_message (msg_lines)
	author, author_email, msgid, date, posting_host = extract_info_from_msg (headers)
	keywords = ''
	modelist = ''
	storage_location = msgnum+'.msg'
	if "save-headers" not in l:
		headers = None

	out = """<?xml version="1.0"?>
<file basic-resource-id="%s" displayname="%s">
	<type simple-mime="image/jpeg" complex-mime="image/jpeg" />
	<versions>
		<version v="1.0">
			<author name="%s" email="%s" ipaddy="%s" />
			<submittor logon="OnetAddNewsgroupMessage" ipaddy="127.0.0.1" date="%s" />
			<storage location="%s" />
		</version>
	</versions>
	<permissions order="allow, deny">
		<grant name="class:modify" to="user:GetNews" />
		<grant name="class:view" to="user:*" />
	</permissions>
	<keywords>
		%s
	</keywords>
	<modelist>
		%s
	</modelist>
	<properties>
		<property name="datetime" simple-type="string" complex-type="time/internet">
			%s
		</property>
		<property name="Message-ID" simple-type="string" complex-type="rfc822/message-id">
			%s
		</property>	
	"""	% 	(resid, dpyname, author, author_email, posting_host, today_date(), 
				storage_location, keywords, modelist, date, cgi_escape (msgid)
			)
	if headers != None:
		out += """		<property name="rfc822-header" author="user:GetNews" type="long_string" encoding="quoted-printable/cgi_escape">"""
		out += qpi_encode(string.join (map(lambda x: cgi_escape(x), headers), '\n'))
		out += """		</property>""" 
	out += """</properties>\n</file>\n"""

	quickWrite (fn+".onet", [out], false)
	
	# --------------------
	#  finish writing xml
	# --------------------

	if extract_contents == 1:
		me2 = MailExtract()
		me2.set_outdir (fn_)
		me2.do_decode_lines (msg_lines)
		
#eof

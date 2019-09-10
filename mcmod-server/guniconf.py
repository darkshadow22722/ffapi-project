import multiprocessing

import gunicorn

from app import config as mcmod

gunicorn.SERVER_SOFTWARE = '{}/{}'.format(mcmod.server_name, mcmod.server_version)

bind = '{}:{}'.format(mcmod.server_host, mcmod.server_port)

workers = multiprocessing.cpu_count() * 2 + 1
worker_class = 'sync'

errorlog = '-'
loglevel = 'info'
accesslog = '-'
access_log_format = '%(h)s %(l)s %(u)s %(t)s "%(r)s" %(s)s %(b)s "%(f)s" "%(a)s"'

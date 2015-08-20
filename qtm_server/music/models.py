from django.db import models

class User(models.Model):
    uuid = models.CharField(max_length=36, unique=True)

    def __unicode__(self):
        return self.uuid

class Account(models.Model):
	# should enforce min_length and non-blank with custom forms later
	user = models.OneToOneField(User, primary_key=True)
	username = models.CharField(max_length=50, unique=True)
	password = models.CharField(max_length=50)

	def __unicode__(self):
		return "{0} | {1}".format(self.user.uuid, self.username)

class Queue(models.Model):
	owner = models.ForeignKey(User)
	name = models.TextField()
	description = models.TextField()
	capacity = models.PositiveSmallIntegerField(default=10)
	last_modified_time = models.DateTimeField(auto_now=True)
	created_time = models.DateTimeField(auto_now_add=True)

	def __unicode__(self):
		return self.name

class Song(models.Model):
	queue = models.ManyToManyField(Queue)
	spotify_id = models.CharField(max_length=36) # this length is just an estimate

	def __unicode__(self):
		return self.spotify_id
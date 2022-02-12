import matplotlib.pyplot as plt
import numpy as np
import os
import PIL
import tensorflow as tf

from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.models import Sequential

import pathlib

## Load in Dataset
#  This is the FER-2013 Dataset for facial expressions
#  Can be found at https://www.kaggle.com/msambare/fer2013
batch_size = 32
img_height = 48
img_width = 48
train_dataset = tf.keras.utils.image_dataset_from_directory(
  pathlib.Path(os.getcwd() + "\\train\\"),
  seed=123,
  image_size=(img_height, img_width),
  batch_size=batch_size)

test_dataset = tf.keras.utils.image_dataset_from_directory(
  pathlib.Path(os.getcwd() + "\\test\\"),
  seed=123,
  image_size=(img_height, img_width),
  batch_size=batch_size)


# Increase performance on both train and test datasets

train_dataset = train_dataset.cache().shuffle(
    1000).prefetch(buffer_size=tf.data.AUTOTUNE)
test_dataset = test_dataset.cache().prefetch(buffer_size=tf.data.AUTOTUNE)


# Set up the training model

model = Sequential([
    # Decrease the range of pixel values from (0-255) to (0-1)
    layers.Rescaling(1./255, input_shape=(img_height, img_width, 3)),

    # Add three convolutional layer blocks with pooling
    layers.Conv2D(16, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),
    layers.Conv2D(32, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),
    layers.Conv2D(64, 3, padding='same', activation='relu'),
    layers.MaxPooling2D(),

    # Flatten the output
    layers.Dropout(0.2),
    layers.Flatten(),

    # Add Dense layers to actually make the model learn
    layers.Dense(128, activation='relu'),
    layers.Dense(7)  # Number of Classes to output
])

model.compile(
        optimizer='adam',
        loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
        metrics=['accuracy'])

history = model.fit(train_dataset, validation_data=test_dataset, epochs=15)

acc = history.history['accuracy']
val_acc = history.history['val_accuracy']

loss = history.history['loss']
val_loss = history.history['val_loss']

epochs_range = range(15)

plt.figure(figsize=(8, 8))
plt.subplot(1, 2, 1)
plt.plot(epochs_range, acc, label='Training Accuracy')
plt.plot(epochs_range, val_acc, label='Validation Accuracy')
plt.legend(loc='lower right')
plt.title('Training and Validation Accuracy')

plt.subplot(1, 2, 2)
plt.plot(epochs_range, loss, label='Training Loss')
plt.plot(epochs_range, val_loss, label='Validation Loss')
plt.legend(loc='upper right')
plt.title('Training and Validation Loss')
plt.show()

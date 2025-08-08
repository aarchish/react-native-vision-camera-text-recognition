import { NativeModules, Platform } from 'react-native';
import type { PhotoOptions, Text } from './types';

export async function PhotoRecognizer(options: PhotoOptions): Promise<Text> {
  const { PhotoRecognizerModule } = NativeModules;
  const { uri, orientation, language } = options;
  if (!uri) {
    throw Error("Can't resolve img uri");
  }
  if (Platform.OS === 'ios') {
    return await PhotoRecognizerModule.process(
      uri.replace('file://', ''),
      orientation || 'portrait',
      language || 'latin'
    );
  } else {
    return await PhotoRecognizerModule.process(
      uri,
      language || 'latin'
    );
  }
}

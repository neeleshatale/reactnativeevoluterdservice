import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-evolute-rdservice' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const EvoluteRdService = NativeModules.EvoluteRDModule
  ? NativeModules.EvoluteRDModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function captureBioMetricDetails(aadharName =``,env=`PP`) {
  return new Promise((resolve, reject) => {
    EvoluteRdService.captureBioMetricDetails(aadharName,env)
      .then((resObj) => {
        resolve(resObj);
      })
      .catch((err) => {
        reject(err);
      });
  });
}
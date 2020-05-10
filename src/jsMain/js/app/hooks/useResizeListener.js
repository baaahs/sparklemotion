import React, { useEffect, useCallback } from 'react';
import throttle from 'lodash/throttle';
import ResizeObserver from 'resize-observer-polyfill';

export function useResizeListener(elementRef, onResized) {
  const onResizedThrottled = useCallback(throttle(onResized, 40), [elementRef]);

  // Fire the callback anytime the ref is resized
  useEffect(() => {
    if (!elementRef?.current) return;

    const ro = new ResizeObserver(onResizedThrottled);
    ro.observe(elementRef.current);

    return () => {
      ro.unobserve(elementRef.current);
    };
  }, [elementRef, onResizedThrottled]);

  // Fire once when the component first mounts
  useEffect(() => {
    const intervalId = setTimeout(() => {
      onResizedThrottled();
    }, 500);

    return () => {
      clearTimeout(intervalId);
    };
  }, [elementRef, onResizedThrottled]);

  return onResized;
}

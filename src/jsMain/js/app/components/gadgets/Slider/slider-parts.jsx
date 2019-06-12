import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import styles from './Slider.scss';

// *******************************************************
// RAIL
// *******************************************************
const railOuterStyle = {
  position: 'absolute',
  height: '100%',
  width: 42,
  transform: 'translate(-50%, 0%)',
  borderRadius: 7,
  cursor: 'pointer',
};

const railInnerStyle = {
  position: 'absolute',
  height: '100%',
  width: 7,
  transform: 'translate(-50%, 0%)',
  borderRadius: 7,
  pointerEvents: 'none',
  boxShadow: `rgba(0, 0, 0, .85) 1px 1px 1px inset, rgba(255, 255, 255, 0.2) -1px -1px 1px inset`,
};

export function SliderRail({ getRailProps }) {
  return (
    <Fragment>
      <div style={railOuterStyle} {...getRailProps()} />
      <div style={railInnerStyle} />
    </Fragment>
  );
}

SliderRail.propTypes = {
  getRailProps: PropTypes.func.isRequired,
};

// *******************************************************
// HANDLE COMPONENT
// *******************************************************
export function Handle({
  domain: [min, max],
  handle: { id, value, percent },
  getHandleProps,
}) {
  return (
    <Fragment>
      <div
        className={styles['slider--touch-area']}
        style={{
          top: `${percent}%`,
          WebkitTapHighlightColor: 'rgba(0,0,0,0)',
        }}
        {...getHandleProps(id)}
      />
      <div
        role="slider"
        className={styles['slider--handle-wrapper']}
        aria-valuemin={min}
        aria-valuemax={max}
        aria-valuenow={value}
        style={{
          top: `${percent}%`,
        }}
      >
        <div className={styles['slider--handle-notch']} />
        <div className={styles['slider--handle-notch']} />
        <div className={styles['slider--handle-notch']} />
        <div
          className={classNames(
            styles['slider--handle-notch'],
            styles['slider--handle-notch-middle']
          )}
        />
        <div
          className={classNames(
            styles['slider--handle-notch'],
            styles['slider--handle-notch-lower']
          )}
        />
        <div
          className={classNames(
            styles['slider--handle-notch'],
            styles['slider--handle-notch-lower']
          )}
        />
        <div
          className={classNames(
            styles['slider--handle-notch'],
            styles['slider--handle-notch-lower']
          )}
        />
      </div>
    </Fragment>
  );
}

Handle.propTypes = {
  domain: PropTypes.array.isRequired,
  handle: PropTypes.shape({
    id: PropTypes.string.isRequired,
    value: PropTypes.number.isRequired,
    percent: PropTypes.number.isRequired,
  }).isRequired,
  getHandleProps: PropTypes.func.isRequired,
};

// *******************************************************
// KEYBOARD HANDLE COMPONENT
// Uses button to allow keyboard events
// *******************************************************
export function KeyboardHandle({
  domain: [min, max],
  handle: { id, value, percent },
  getHandleProps,
}) {
  return (
    <button
      role="slider"
      aria-valuemin={min}
      aria-valuemax={max}
      aria-valuenow={value}
      style={{
        top: `${percent}%`,
        position: 'absolute',
        transform: 'translate(-50%, -50%)',
        width: 24,
        height: 24,
        zIndex: 5,
        cursor: 'pointer',
        border: 0,
        borderRadius: '50%',
        boxShadow: '1px 1px 1px 1px rgba(0, 0, 0, 0.3)',
        backgroundColor: '#6ABBC0',
      }}
      {...getHandleProps(id)}
    />
  );
}

KeyboardHandle.propTypes = {
  domain: PropTypes.array.isRequired,
  handle: PropTypes.shape({
    id: PropTypes.string.isRequired,
    value: PropTypes.number.isRequired,
    percent: PropTypes.number.isRequired,
  }).isRequired,
  getHandleProps: PropTypes.func.isRequired,
};

// *******************************************************
// TRACK COMPONENT
// *******************************************************
export function Track({ source, target, getTrackProps }) {
  return (
    <div
      style={{
        position: 'absolute',
        zIndex: 1,
        backgroundColor: '#6ABBC0',
        borderRadius: 7,
        cursor: 'pointer',
        width: 14,
        transform: 'translate(-50%, 0%)',
        top: `${source.percent}%`,
        height: `${target.percent - source.percent}%`,
      }}
      {...getTrackProps()}
    />
  );
}

Track.propTypes = {
  source: PropTypes.shape({
    id: PropTypes.string.isRequired,
    value: PropTypes.number.isRequired,
    percent: PropTypes.number.isRequired,
  }).isRequired,
  target: PropTypes.shape({
    id: PropTypes.string.isRequired,
    value: PropTypes.number.isRequired,
    percent: PropTypes.number.isRequired,
  }).isRequired,
  getTrackProps: PropTypes.func.isRequired,
};

// *******************************************************
// TICK COMPONENT
// *******************************************************
export function Tick({ tick, format }) {
  return (
    <div>
      <div
        style={{
          position: 'absolute',
          marginTop: -20,
          marginLeft: 10,
          height: 1,
          width: 6,
          backgroundColor: 'rgb(200,200,200)',
          bottom: `${100 - tick.percent}%`,
        }}
      />
      <div
        style={{
          position: 'absolute',
          marginTop: -5,
          marginLeft: 20,
          fontSize: 10,
          color: '#aeaeae',
          bottom: `${100 - tick.percent}%`,
          transform: `translateY(50%)`,
        }}
      >
        {format(Math.floor(tick.value * 100))}
      </div>
    </div>
  );
}

Tick.propTypes = {
  tick: PropTypes.shape({
    id: PropTypes.string.isRequired,
    value: PropTypes.number.isRequired,
    percent: PropTypes.number.isRequired,
  }).isRequired,
  format: PropTypes.func.isRequired,
};

Tick.defaultProps = {
  format: (d) => d,
};
